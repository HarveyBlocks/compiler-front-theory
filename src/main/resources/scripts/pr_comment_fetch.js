// 使用示例:
// node pr_comment_fetch.js ./config-example.json


const https = require('node:https');
const fs = require('node:fs');
const path = require('node:path');

// ============================================================
// 1. 配置读取
// ============================================================
const configFile = process.argv[2];
if (!configFile) {
	console.error('Usage: node fetch_review.js <config1.json>');
	process.exit(1);
}
const configPath = path.resolve(configFile);
if (!fs.existsSync(configPath)) {
	console.error(`Config file not found: ${configPath}`);
	process.exit(1);
}
let config;
try {
	config = JSON.parse(fs.readFileSync(configPath, 'utf8'));
} catch (e) {
	console.error('Invalid JSON:', e.message);
	process.exit(1);
}
const { token, owner, repo, pr, contextLines = 3, outputFile } = config;
if (!token || !owner || !repo || !pr) {
	console.error('Config must contain: token, owner, repo, pr');
	process.exit(1);
}

// ============================================================
// 2. HTTP 基础
// ============================================================
const HEADERS = {
	Authorization: `Bearer ${token}`,
	'User-Agent': 'gh-review2md',
	Accept: 'application/vnd.github+json',
};

function get(url) {
	return new Promise((resolve, reject) => {
		https.get(url, { headers: HEADERS }, (res) => {
			let data = '';
			res.on('data', chunk => data += chunk);
			res.on('end', () => {
				try { resolve({ data: JSON.parse(data), headers: res.headers }); }
				catch (e) { reject(e); }
			});
		}).on('error', reject);
	});
}

function getNextPage(headers) {
	const link = headers.link;
	if (!link) return null;
	const match = link.match(/<([^>]+)>;\s*rel="next"/);
	return match ? match[1] : null;
}

async function paginate(url) {
	const results = [];
	let current = url;
	while (current) {
		const { data, headers } = await get(current);
		results.push(...data);
		current = getNextPage(headers);
	}
	return results;
}

// ============================================================
// 3. 文件内容与缓存
// ============================================================
const fileCache = new Map();

async function fetchFileContent(commitSha, filePath) {
	const cacheKey = `${commitSha}:${filePath}`;
	if (fileCache.has(cacheKey)) return fileCache.get(cacheKey);

	const url = `https://api.github.com/repos/${owner}/${repo}/contents/${filePath}?ref=${commitSha}`;
	try {
		const { data } = await get(url);
		const text = data.content && data.encoding === 'base64'
				? Buffer.from(data.content, 'base64').toString('utf8')
				: null;
		fileCache.set(cacheKey, text);
		return text;
	} catch {
		fileCache.set(cacheKey, null);
		return null;
	}
}

// ============================================================
// 4. 上下文与语言
// ============================================================
function extractContext(content, targetLine, contextSize) {
	const lines = content.split('\n');
	const idx = targetLine - 1;
	if (idx < 0 || idx >= lines.length) return null;

	const start = Math.max(0, idx - contextSize);
	const end = Math.min(lines.length, idx + contextSize + 1);
	return lines.slice(start, end).map((line, i) => {
		const lineNumber = start + i + 1;
		const marker = lineNumber === targetLine ? '> ' : '  ';
		return `${marker}${lineNumber}: ${line}`;
	}).join('\n');
}

const EXT_TO_LANG = {
	'.js': 'javascript', '.ts': 'typescript', '.py': 'python', '.java': 'java',
	'.go': 'go', '.rs': 'rust', '.rb': 'ruby', '.c': 'c', '.cpp': 'cpp',
	'.css': 'css', '.html': 'html', '.md': 'markdown', '.json': 'json',
	'.yml': 'yaml', '.yaml': 'yaml', '.sh': 'bash', '.sql': 'sql',
};

function guessLanguage(filePath) {
	const ext = path.extname(filePath).toLowerCase();
	return EXT_TO_LANG[ext] || '';
}

// ============================================================
// 5. Markdown 渲染（按职责拆分）
// ============================================================

/** 渲染 PR 整体评论区块 */
function renderPrCommentSection(issueComments) {
	const md = [];
	if (issueComments.length === 0) return md;
	md.push('## PR Comments', '');
	for (const c of issueComments) {
		md.push(`**${c.user.login}** commented on ${c.created_at}:`, '');
		md.push(c.body, '', '---', '');
	}
	return md;
}

/** 渲染单条代码评论（含上下文） */
async function renderSingleReviewComment(comment, contextSize) {
	const md = [];
	const filePath = comment.path || '(no file)';

	if (comment.line) {
		md.push(`- **Line**: ${comment.line}`);
	} else {
		md.push(`- **File-level comment**`);
	}
	md.push('');

	if (comment.line && comment.commit_id) {
		const content = await fetchFileContent(comment.commit_id, filePath);
		if (content) {
			const snippet = extractContext(content, comment.line, contextSize);
			if (snippet) {
				const lang = guessLanguage(filePath);
				md.push('```' + lang);
				md.push(snippet);
				md.push('```', '');
			} else {
				md.push('> Line number out of range', '');
			}
		} else {
			md.push('> Could not fetch file content', '');
		}
	}

	md.push(`> ${comment.body.replace(/\n/g, '\n> ')}`);
	md.push('');
	return md;
}

/** 渲染所有代码评论区块（按文件分组） */
async function renderCodeCommentsSection(reviewComments, contextSize) {
	const md = [];
	if (reviewComments.length === 0) return md;

	const byFile = new Map();
	for (const c of reviewComments) {
		const filePath = c.path || '(no file)';
		if (!byFile.has(filePath)) byFile.set(filePath, []);
		byFile.get(filePath).push(c);
	}

	md.push('## Code Review Comments', '');
	for (const [filePath, comments] of byFile) {
		md.push(`### ${filePath}`, '');
		for (const c of comments) {
			md.push(...(await renderSingleReviewComment(c, contextSize)));
		}
		md.push('---', '');
	}
	return md;
}

/** 主编排函数：只负责组合各区块 */
async function generateMarkdown(issueComments, reviewComments, contextSize) {
	const parts = [
		[`# PR Review Report`],
		[`**PR**: ${owner}/${repo}#${pr}  |  Context lines: ±${contextSize}`, ''],
		renderPrCommentSection(issueComments),
		await renderCodeCommentsSection(reviewComments, contextSize),
		['*Report generated by gh-review2md*'],
	];

	// flat() 把嵌套数组展平，然后 join 成最终文本
	return parts.flat().join('\n');
}

// ============================================================
// 6. 主流程
// ============================================================
(async () => {
	try {
		console.error(`Using config: ${configPath}`);
		console.error('Fetching PR comments...');
		const issueUrl = `https://api.github.com/repos/${owner}/${repo}/issues/${pr}/comments`;
		const reviewUrl = `https://api.github.com/repos/${owner}/${repo}/pulls/${pr}/comments`;

		console.log('issueUrl: ',issueUrl);
		console.log('reviewUrl: ',reviewUrl);
		const [issueComments, reviewComments] = await Promise.all([
			paginate(issueUrl),
			paginate(reviewUrl),
		]);

		console.error(`Found ${issueComments.length} PR comments, ${reviewComments.length} review comments.`);
		console.error('Generating Markdown...');
		const markdown = await generateMarkdown(issueComments, reviewComments, contextLines);

		if (outputFile) {
			fs.writeFileSync(outputFile, markdown, 'utf8');
			console.error(`Output written to ${outputFile}`);
		} else {
			console.log(markdown);
		}
	} catch (err) {
		console.error('Error:', err.message);
		process.exit(1);
	}
})();
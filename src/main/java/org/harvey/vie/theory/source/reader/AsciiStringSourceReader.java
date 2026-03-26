package org.harvey.vie.theory.source.reader;

import lombok.Getter;
import org.harvey.vie.theory.error.ErrorContext;
import org.harvey.vie.theory.error.IoErrorMessage;
import org.harvey.vie.theory.exception.CompileException;
import org.harvey.vie.theory.source.character.AsciiCharacter;
import org.harvey.vie.theory.source.character.SourceCharacter;

import java.io.IOException;
import java.io.StringReader;

/**
 * AN implementation of {@link SourceReader} that reads characters from a string.
 * This reader is restricted to ASCII characters and will report an error if
 * a non-ASCII character is encountered in the source.
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-03-24 21:56
 */
public class AsciiStringSourceReader implements SourceReader {
    private final StringReader reader;
    private final ErrorContext errorContext;
    @Getter
    private int offset;

    public AsciiStringSourceReader(StringReader reader, ErrorContext errorContext) {
        this.reader = reader;
        this.errorContext = errorContext;
        this.offset = 0;
    }

    @Override
    public SourceCharacter read() throws IOException, CompileException {
        offset++;
        int ch = reader.read();
        if (ch==-1){
            return SourceCharacter.EOF;
        }
        // 仅支持 ascii
        if ((ch & 0xff_ff_ff_00) > 0) {
            errorContext.addError(new IoErrorMessage(offset,"ascii only"));
            throw new CompileException();
        }
        return new AsciiCharacter((byte) ch);
    }

    @Override
    public SourceCharacter peek() throws IOException, CompileException {
        reader.mark(1);
        SourceCharacter ch = read();
        offset--;
        reader.reset();
        return ch;
    }

    @Override
    public void close() {
        reader.close();
    }
}

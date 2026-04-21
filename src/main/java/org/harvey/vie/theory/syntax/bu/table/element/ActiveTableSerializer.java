package org.harvey.vie.theory.syntax.bu.table.element;

import org.harvey.vie.theory.io.Loaders;
import org.harvey.vie.theory.io.Storages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO 不存储长宽
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2026-04-21 11:33
 */
public class ActiveTableSerializer {
    public static int store(ActiveTableElement[][] table, OutputStream os) throws IOException {
        int len = 0;
        for (ActiveTableElement[] line : table) {
            for (ActiveTableElement element : line) {
                if (element == null) {
                    len += Storages.storeInteger(os, 0);
                } else if (element.isShift()) {
                    len += Storages.storeInteger(os, element.nextStatus() + 1);
                } else {
                    len += Storages.storeInteger(os, -element.getProduction() - 1);
                }
            }
        }
        return len;
    }

    public static ActiveTableElement[][] load(int acceptStatus, int endMark, Factory arrayFactory, InputStream is)
            throws IOException {
        ActiveTableElement[][] table = arrayFactory.instance();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                int n = Loaders.loadInteger(is);
                if (i == acceptStatus && j == endMark) {
                    table[i][j] = new AcceptTableElementImpl(-n - 1);
                } else if (n < 0) {
                    table[i][j] = new ReduceTableElementImpl(-n - 1);
                } else if (n > 0) {
                    table[i][j] = new ShiftTableElementImpl(n - 1);
                }
            }
        }
        return table;
    }

    public static interface Factory {
        ActiveTableElement[][] instance();
    }
}

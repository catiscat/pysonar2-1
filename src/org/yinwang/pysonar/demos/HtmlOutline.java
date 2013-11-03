package org.yinwang.pysonar.demos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.pysonar.Indexer;
import org.yinwang.pysonar.Outliner;
import org.yinwang.pysonar.Util;

import java.util.List;

/**
 * Generates a static-html outline for a file.
 */
class HtmlOutline {

    private Indexer indexer;
    @Nullable
    private StringBuilder buffer;

    public HtmlOutline(Indexer idx) {
        this.indexer = idx;
    }

    /**
     * Generates HTML outline for {@code path}.
     * @return the html as an {@code UL} element
     */
    @NotNull
    public String generate(String path) {
        buffer = new StringBuilder(1024);
        List<Outliner.Entry> entries = indexer.generateOutline(path);
        addOutline(entries);
        String html = buffer.toString();
        buffer = null;
        return html;
    }

    private void addOutline(@NotNull List<Outliner.Entry> entries) {
        add("<ul>\n");
        for (Outliner.Entry e : entries) {
            addEntry(e);
        }
        add("</ul>\n");
    }

    private void addEntry(@NotNull Outliner.Entry e) {
        add("<li>");

        String style = null;
        switch (e.getKind()) {
            case FUNCTION:
            case METHOD:
            case CONSTRUCTOR:
                style = "function";
                break;
            case CLASS:
                style = "type-name";
                break;
            case PARAMETER:
                style = "parameter";
                break;
            case VARIABLE:
            case SCOPE:
                style = "identifier";
                break;
        }

        add("<a href='#");
        add(e.getQname());
        add("', onmouseover='highlight(\"" + Util.escapeQname_(e.getQname()) + "\")'>");

        if (style != null) {
            add("<span class='");
            add(style);
            add("'>");
        }
        add(e.getName());
        if (style != null) {
            add("</span>");
        }

        add("</a>");

        if (e.isBranch()) {
            addOutline(e.getChildren());
        }
        add("</li>");
    }

    private void add(String text) {
        buffer.append(text);
    }
}

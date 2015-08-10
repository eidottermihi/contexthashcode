package hudson.plugins.analysis.util;

import java.io.*;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.CheckForNull;

/**
 * Creates a hash code from the source code of the warning line and the
 * surrounding context.
 *
 * @author Ulli Hafner
 */
public class ContextHashCode {
    /** Number of lines before and after current line to consider. */
    private static final int LINES_LOOK_AHEAD = 3;
    private static final int BUFFER_SIZE = 1000;

    /**
     * Creates a hash code from the source code of the warning line and the
     * surrounding context.
     *
     * @param fileName
     *            the absolute path of the file to read
     * @param line
     *            the line of the warning
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return a has code of the source code
     * @throws java.io.IOException
     *             if the contents of the file could not be read
     */
    public int create(final String fileName, final int line, final String encoding) throws IOException {
        LineIterator lineIterator = readFile(fileName, encoding);

        StringBuilder context = new StringBuilder(BUFFER_SIZE);
        for (int i = 0; lineIterator.hasNext(); i++) {
            String currentLine = lineIterator.nextLine();
            if (i >= line - LINES_LOOK_AHEAD) {
                context.append(currentLine);
            }
            if (i > line + LINES_LOOK_AHEAD) {
                break;
            }
        }
        lineIterator.close();

        return context.toString().hashCode();
    }

    /**
     * Reads the specified file with the given encoding.
     *
     * @param fileName
     *            the file name
     * @param encoding
     *            the encoding of the file, if <code>null</code> or empty then
     *            the default encoding of the platform is used
     * @return the line iterator
     * @throws java.io.FileNotFoundException
     *             Indicates that the file is not found.
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred during reading of
     *             the file.
     */
    public static LineIterator readFile(final String fileName, @CheckForNull final String encoding)
            throws FileNotFoundException, IOException {
        FileInputStream stream = new FileInputStream(new File(fileName));
        if (StringUtils.isNotBlank(encoding)) {
            return IOUtils.lineIterator(stream, encoding);
        }
        else {
            return new LineIterator(new InputStreamReader(stream, Charsets.toCharset(Charset.defaultCharset())));
        }
    }
}


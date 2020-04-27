package ca.canada.treasury.testbed.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;

/**
 * Utility methods.
 * @author Pascal Essiembre
 */
public final class IndexerUtil {

    public static final DateTimeFormatter SOLR_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private IndexerUtil() {
    }

    public static boolean isGZipped(File f) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            return GZIPInputStream.GZIP_MAGIC
                    == (raf.read() & 0xff | ((raf.read() << 8) & 0xff00));
        }
    }

    public static String capitalizeFully(String s) {
        if (s == null) {
            return null;
        }
        return StringUtils.capitalize(s.toLowerCase(Locale.FRENCH));
    }

    /**
     * Returns a line iterator for both plain text files and GZipped ones.
     * This method checks that the supplied file is not <code>null</code> and
     * actually is a file.
     * @param f file
     * @return line iterator
     * @throws IOException could not read file
     */
    public static LineIterator lineIterator(File f) throws IOException {
        return IOUtils.lineIterator(toInputStream(f), StandardCharsets.UTF_8);
    }

    /**
     * Gets a Reader for both plain text files and GZipped ones. This method
     * checks that the supplied file is not <code>null</code> and actually
     * is a file.
     * @param f file
     * @return reader
     * @throws IOException could not read file
     */
    public static Reader toReader(File f) throws IOException {
        return new InputStreamReader(toInputStream(f), StandardCharsets.UTF_8);
    }
    /**
     * Gets an InputStream for both plain text files and GZipped ones.
     * This method checks that the supplied file is not <code>null</code>
     * and actually is a file.
     * @param f file
     * @return input stream
     * @throws IOException could not read file
     */
    public static InputStream toInputStream(File f) throws IOException {
        Objects.requireNonNull("'file' must not be null.");
        if (!f.isFile()) {
            throw new IOException("Not a file: " + f.getAbsolutePath());
        }
        if (isGZipped(f)) {
            return new GZIPInputStream(new FileInputStream(f));
        }
        return new FileInputStream(f);
    }

    /**
     * Gets a Writer that can be GZip compressed.
     * @param f file
     * @param compress <code>true</code> to use GZip compression
     * @return output stream
     * @throws IOException could not write file
     */
    public static Writer toWriter(File f, boolean compress) throws IOException {
        return new OutputStreamWriter(
                toOutputStream(f, compress), StandardCharsets.UTF_8);
    }
    /**
     * Gets an OutputStream that can be GZip compressed.
     * @param f file
     * @param compress <code>true</code> to use GZip compression
     * @return output stream
     * @throws IOException could not write file
     */
    public static OutputStream toOutputStream(File f, boolean compress)
            throws IOException {
        if (compress) {
            return new GZIPOutputStream(new FileOutputStream(f));
        }
        return new FileOutputStream(f);
    }

    public static String toSolrDateTime(Instant dateTime) {
        return toSolrDateTime(dateTime.atZone(ZoneOffset.UTC));
    }
    public static String toSolrDateTime(ZonedDateTime dateTime) {
        return SOLR_DATETIME_FORMATTER.format(
                dateTime.withZoneSameInstant(ZoneOffset.UTC));
    }
    public static String toSolrDateTime(long epochMilli) {
        return toSolrDateTime(
                Instant.ofEpochMilli(epochMilli).atZone(ZoneOffset.UTC));
    }

    public static SolrInputDocument toSolrDocument(Map<String, Object> doc) {
        SolrInputDocument solrDoc = new SolrInputDocument();
        for (Entry<String, Object> en : doc.entrySet()) {
            solrDoc.addField(en.getKey(), en.getValue());
        }
        return solrDoc;
    }

    /**
     * Converts to string in a "smarter" way for search.
     * If the object is a collection or array, it performs a join with a comma
     * (blank and null values are ignored). If the object is a database
     * {@link Timestamp}, it converts to a Solr date string.
     * Else, performs a regular toString().
     * @param obj the object to convert to string
     * @return the string or <code>null</code> if the object is
     *         <code>null</code>.
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        if (obj.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj); i ++) {
                append(b, Array.get(obj, i));
            }
        } else if (obj instanceof Collection) {
            for (Object o : (Collection<?>) obj) {
                append(b, o);
            }
        } else if (obj instanceof Timestamp) {
            append(b, toSolrDateTime(((Timestamp) obj).toInstant()));
        } else {
            append(b, obj);
        }
        return b.toString();
    }
    private static void append(StringBuilder b, Object value) {
        if (value != null) {
            String s = value.toString();
            if (StringUtils.isNotBlank(s)) {
                if (b.length() > 0) {
                    b.append(", ");
                }
                b.append(s);
            }
        }
    }


    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return StringUtils.isBlank((String) obj);
        }
        if (obj.getClass().isArray()) {
            if (Array.getLength(obj) == 0) {
                return true;
            }
            if (Array.getLength(obj) == 1) {
                return isEmpty(Array.get(obj, 0));
            }
            return false;
        }
        if (obj instanceof Collection) {
            Collection<?> c = (Collection<?>) obj;
            if (c.isEmpty()) {
                return true;
            }
            if (c.size() == 1) {
                return isEmpty(c.iterator().next());
            }
        }
        return false;
    }

    public static Object stripHtml(Object obj) {
//        return onString(obj, IndexerUtil::stripHtml);

        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return stripHtml((String) obj);
        }
        if (obj.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj); i ++) {
                Object value = Array.get(obj, i);
                if (value instanceof String) {
                    Array.set(obj, i, stripHtml((String) value));
                }
            }
        } else if (obj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            for (int i = 0; i < list.size(); i ++) {
                Object value = list.get(i);
                if (value instanceof String) {
                    list.set(i, stripHtml((String) value));
                }
            }
        }
        return obj;
    }
    public static String stripHtml(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\<.*?\\>", "");
    }

//    public static Object onString(Object obj, Function<String, Object> f) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof String) {
//            f.apply((String) obj);
//        }
//        if (obj.getClass().isArray()) {
//            for (int i = 0; i < Array.getLength(obj); i ++) {
//                Object value = Array.get(obj, i);
//                if (value instanceof String) {
//                    Array.set(obj, i, f.apply((String) value));
//                }
//            }
//        } else if (obj instanceof List<?>) {
//            @SuppressWarnings("unchecked")
//            List<Object> list = (List<Object>) obj;
//            for (int i = 0; i < list.size(); i ++) {
//                Object value = list.get(i);
//                if (value instanceof String) {
//                    list.set(i, f.apply((String) value));
//                }
//            }
//        }
//        return obj;
//    }


}

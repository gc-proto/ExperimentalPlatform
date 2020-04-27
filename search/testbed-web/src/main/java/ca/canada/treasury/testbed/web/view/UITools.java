package ca.canada.treasury.testbed.web.view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.noggit.CharArr;
import org.noggit.JSONWriter;

import ca.canada.treasury.testbed.web.model.SearchCluster;

public final class UITools {

    private static final UITools INSTANCE = new UITools();

//    private static final Logger LOG = LogManager.getLogger(UITools.class);
//
//    private static final Map<String, String> FILE_ICONS = MapUtils.putAll(
//            new HashMap<>(), new String[] {
//        "wordprocessor", "far fa-file-word",
//        "spreadsheet", "far fa-file-excel",
//        "xml", "far fa-file-code",
//        "html", "far fa-file-code",
//        "audio", "far fa-file-audio",
//        "presentation", "far fa-file-powerpoint",
//        "pdf", "far fa-file-pdf",
//        "text", "far fa-file-alt",
//        "image", "far fa-file-image",
//        "video", "far fa-file-video",
//        "vector", "far fa-file-image",
//        "archive", "far fa-file-archive",
//        "email", "far fa-envelope-open"
//    });
//    private static final String DEFAULT_FILE_ICON = "far fa-file";
//
//
//    public String fileIcon(String contentFamily) {
//        if (StringUtils.isBlank(contentFamily)) {
//            return DEFAULT_FILE_ICON;
//        }
//        String icon = FILE_ICONS.get(contentFamily);
//        if (icon == null) {
//            return DEFAULT_FILE_ICON;
//        }
//        return icon;
//    }
//
//    public String join(Collection<String> collection) {
//        return StringUtils.join(collection, ", ");
//    }

    private UITools() {
        super();
    }

    public static UITools instance() {
        return INSTANCE;
    }

    public String formatNumber(Long number) {
        if (number == null) {
            return null;
        }
        return NumberFormat.getNumberInstance(Locale.ENGLISH).format(number);
    }

    // assumes UTC
    public String formatDateTime(LocalDateTime dt) {
        if (dt == null) {
            return null;
        }
        return dt.format(DateTimeFormatter.ofPattern(
                "MMM d, yyyy 'at' H:mm", Locale.ENGLISH));
    }
    public String formatDate(LocalDate d) {
        if (d == null) {
            return null;
        }
        String s = d.format(DateTimeFormatter.ofPattern(
                "MMM d, yyyy", Locale.ENGLISH));
        if (!StringUtils.startsWithIgnoreCase(s, "may")) {
            s = s.replaceFirst("(\\w+)(.*)", "$1.$2");
        }
        return s;
    }

    public String toJQCloudArray(List<SearchCluster> clusters) {
        return toJQCloudArray(clusters, -1);
    }
    public String toJQCloudArray(List<SearchCluster> clusters, int max) {
        CharArr out = new CharArr();
        JSONWriter json = new JSONWriter(out);
        json.startArray();
        int idx = 0;
        for (SearchCluster c : clusters) {

            if (idx > -1 && idx == max) {
                break;
            }

            //TODO FOR NOW remove "other topics" (until handled)
            if ("other topics".equalsIgnoreCase(c.getLabel())) {
                continue;
            }

            if (idx > 0) {
                json.writeValueSeparator();
            }

            json.startObject();

            json.writeString("text");
            json.writeNameSeparator();
            json.writeString(c.getLabel());

            json.writeValueSeparator();

            json.writeString("weight");
            json.writeNameSeparator();
            json.write(c.getDocIds().size());

            json.writeValueSeparator();

            json.writeString("link");
            json.writeNameSeparator();
            json.writeString("#");

            json.writeValueSeparator();

            json.writeString("html");
            json.writeNameSeparator();
            json.startObject();
                json.writeString("class");
                json.writeNameSeparator();
                json.writeString("cluster-label");

                json.writeValueSeparator();

                json.writeString("data-index");
                json.writeNameSeparator();
                json.write(idx++);

                json.writeValueSeparator();

                json.writeString("data-docids");
                json.writeNameSeparator();
                json.write(c.getDocIds());

            json.endObject();

            json.endObject();
        }

        json.endArray();

        return out.toString();
    }


//    public String formatDate(Object date) {
//        if (date == null) {
//            return null;
//        }
//
//        Date d = null;
//        if (date instanceof Date) {
//            d = (Date) date;
//        } else if (date instanceof Long) {
//            d = new Date((Long) date);
//        } else if (date instanceof String) {
//            String s = (String) date;
//            if (NumberUtils.isDigits(s)) {
//                d = new Date(NumberUtils.toLong(s));
//            } else {
//                try {
//                    d = DateFormat.getDateInstance(
//                            DateFormat.FULL, Locale.ENGLISH).parse(s);
//                } catch (ParseException e) {
//                    LOG.warn("Could not parse date: {}",  date, e);
//                    return null;
//                }
//            }
//        } else {
//            LOG.warn("Could not parse date: {}",  date);
//            return null;
//        }
//        return FastDateFormat.getInstance("yyyy-MM-dd").format(d);
//    }
//
//    public String formatSize(Object size) {
//        if (size == null) {
//            return null;
//        }
//
//        long z = 0;
//        if (size instanceof Long) {
//            z = (Long) size;
//        } else if (size instanceof String
//                && NumberUtils.isDigits((String) size)) {
//            z = NumberUtils.toLong((String) size);
//        } else {
//            LOG.warn("Could not parse size: {}",  size);
//            return null;
//        }
//        return new DataUnitFormatter(Locale.ENGLISH, 1).format(z, DataUnit.B);
//    }
//
//    public String formatFileType(String fileType) {
//        if (fileType == null) {
//            return null;
//        }
//        ContentFamily cf = ContentFamily.valueOf(fileType);
//        if (cf == null) {
//            return null;
//        }
//        return cf.getDisplayName(Locale.ENGLISH);
//    }

}

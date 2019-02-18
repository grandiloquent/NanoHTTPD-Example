package euphoria.psycho;

import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

class FileUtils {

    public static String formatFileSize(long number) {
        float result = number;
        String suffix = "";
        if (result > 900) {
            suffix = " KB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " MB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " PB";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.1f", result);
        } else if (result < 100) {
            value = String.format("%.0f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return value + suffix;
    }

    public static String getExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) return name.substring(dotIndex + 1);
        return null;
    }

    public static void sortFiles(File[] files, int sortBy, boolean ascend) {
        // 0 name
        // 1 last modified
        // 2 size

        Collator collator = Collator.getInstance(Locale.CHINA);
        Arrays.sort(files, (o1, o2) -> {
            boolean b1 = o1.isDirectory();
            boolean b2 = o2.isDirectory();
            if (b1 == b2) {

                switch (sortBy) {
                    case 0:
                        int compare = collator.compare(o1.getName(), o2.getName());
                        return ascend ? compare :
                                compare * -1;
                    case 1:
                        long difLastModified = o1.lastModified() - o2.lastModified();
                        if (difLastModified > 0) {
                            return ascend ? 1 : -1;
                        } else if (difLastModified < 0) {
                            return ascend ? -1 : 1;
                        } else {
                            return 0;
                        }
                    case 2:
                        if (b1) return 0;
                        long difSize = o1.length() - o2.length();
                        if (difSize > 0) {
                            return ascend ? 1 : -1;
                        } else if (difSize < 0) {
                            return ascend ? -1 : 1;
                        } else {
                            return 0;
                        }
                    default:
                        return 0;
                }

            } else if (b1) {

                return ascend ? -1 : 1;
            } else {
                return ascend ? 1 : -1;
            }
        });
    }
}

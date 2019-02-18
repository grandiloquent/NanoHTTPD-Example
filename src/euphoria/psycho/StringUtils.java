package euphoria.psycho;

class StringUtils {

    public static boolean isDigit(String value) {
        for (int i = 0, len = value.length(); i < len; i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return true;
    }

    public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}

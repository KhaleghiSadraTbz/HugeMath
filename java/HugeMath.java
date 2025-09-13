import java.util.*;

public class HugeMath {
    // --- Helpers ---
    private static class Normalized {
        String digits;
        int scale;
        boolean negative;
        Normalized(String digits, int scale, boolean negative) {
            this.digits = digits;
            this.scale = scale;
            this.negative = negative;
        }
    }

    private static Normalized Normalize(String s) {
        s = s.trim();
        boolean negative = s.startsWith("-");
        if (negative) s = s.substring(1);

        int scale = 0;
        if (s.contains(".")) {
            int dot = s.indexOf(".");
            scale = s.length() - dot - 1;
            s = s.substring(0, dot) + s.substring(dot + 1);
        }

        s = s.replaceFirst("^0+", "");
        if (s.equals("")) s = "0";

        return new Normalized(s, scale, negative);
    }

    private static String InsertDecimal(String digits, int scale, boolean negative) {
        if (scale > 0) {
            if (digits.length() <= scale)
                digits = String.format("%" + (scale + 1) + "s", digits).replace(' ', '0');
            int pointPos = digits.length() - scale;
            digits = digits.substring(0, pointPos) + "." + digits.substring(pointPos);
        }

        if (digits.contains(".")) {
            digits = digits.replaceAll("0+$", "");
            digits = digits.replaceAll("\\.$", "");
        }

        if (digits.equals("")) digits = "0";
        if (negative && !digits.equals("0")) digits = "-" + digits;
        return digits;
    }

    private static int CompareAbs(String a, String b) {
        if (a.length() != b.length()) return Integer.compare(a.length(), b.length());
        return a.compareTo(b);
    }

    private static String AddAbs(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;

        int maxLen = Math.max(a.length(), b.length());
        a = String.format("%" + maxLen + "s", a).replace(' ', '0');
        b = String.format("%" + maxLen + "s", b).replace(' ', '0');

        for (int i = a.length() - 1; i >= 0; i--) {
            int sum = (a.charAt(i) - '0') + (b.charAt(i) - '0') + carry;
            carry = sum / 10;
            sb.insert(0, (char) ('0' + (sum % 10)));
        }
        if (carry > 0) sb.insert(0, (char) ('0' + carry));
        return sb.toString();
    }

    private static String SubAbs(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int borrow = 0;

        int maxLen = Math.max(a.length(), b.length());
        a = String.format("%" + maxLen + "s", a).replace(' ', '0');
        b = String.format("%" + maxLen + "s", b).replace(' ', '0');

        for (int i = a.length() - 1; i >= 0; i--) {
            int diff = (a.charAt(i) - '0') - (b.charAt(i) - '0') - borrow;
            if (diff < 0) {
                diff += 10;
                borrow = 1;
            } else borrow = 0;

            sb.insert(0, (char) ('0' + diff));
        }

        String res = sb.toString().replaceFirst("^0+", "");
        return res.equals("") ? "0" : res;
    }

    private static String MulAbs(String a, String b) {
        int[] result = new int[a.length() + b.length()];

        for (int i = a.length() - 1; i >= 0; i--) {
            for (int j = b.length() - 1; j >= 0; j--) {
                int mul = (a.charAt(i) - '0') * (b.charAt(j) - '0');
                int sum = result[i + j + 1] + mul;
                result[i + j + 1] = sum % 10;
                result[i + j] += sum / 10;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int num : result) {
            if (!(sb.length() == 0 && num == 0)) sb.append(num);
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }

    private static String DivAbs(String a, String b, int precision) {
        if (b.equals("0")) throw new ArithmeticException("Divide by zero");

        StringBuilder result = new StringBuilder();
        StringBuilder dividend = new StringBuilder();
        int idx = 0;

        while (result.length() < precision + a.length()) {
            if (idx < a.length()) dividend.append(a.charAt(idx++));
            else dividend.append('0');

            String current = dividend.toString().replaceFirst("^0+", "");
            if (current.equals("")) current = "0";

            int count = 0;
            while (CompareAbs(current, b) >= 0) {
                current = SubAbs(current, b);
                count++;
            }

            result.append(count);
            dividend.setLength(0);
            dividend.append(current);
        }

        String res = result.toString().replaceFirst("^0+", "");
        return res.equals("") ? "0" : res;
    }

    // --- Public Methods ---
    public static String Add(String a, String b) {
        Normalized x = Normalize(a);
        Normalized y = Normalize(b);

        int scale = Math.max(x.scale, y.scale);
        x.digits = String.format("%-" + (x.digits.length() + (scale - x.scale)) + "s", x.digits).replace(' ', '0');
        y.digits = String.format("%-" + (y.digits.length() + (scale - y.scale)) + "s", y.digits).replace(' ', '0');

        String res;
        boolean negative;

        if (x.negative == y.negative) {
            res = AddAbs(x.digits, y.digits);
            negative = x.negative;
        } else {
            int cmp = CompareAbs(x.digits, y.digits);
            if (cmp == 0) return "0";
            if (cmp > 0) {
                res = SubAbs(x.digits, y.digits);
                negative = x.negative;
            } else {
                res = SubAbs(y.digits, x.digits);
                negative = y.negative;
            }
        }

        return InsertDecimal(res, scale, negative);
    }

    public static String Subtract(String a, String b) {
        return Add(a, (b.startsWith("-") ? b.substring(1) : "-" + b));
    }

    public static String Multiply(String a, String b) {
        Normalized x = Normalize(a);
        Normalized y = Normalize(b);

        String res = MulAbs(x.digits, y.digits);
        int scale = x.scale + y.scale;
        boolean negative = x.negative ^ y.negative;

        return InsertDecimal(res, scale, negative);
    }

    public static String Divide(String a, String b, int precision) {
        Normalized x = Normalize(a);
        Normalized y = Normalize(b);

        int scale = precision + Math.max(x.scale, y.scale);
        x.digits = String.format("%-" + (x.digits.length() + (scale - x.scale)) + "s", x.digits).replace(' ', '0');
        y.digits = String.format("%-" + (y.digits.length() + (scale - y.scale)) + "s", y.digits).replace(' ', '0');

        String res = DivAbs(x.digits, y.digits, precision);
        boolean negative = x.negative ^ y.negative;

        return InsertDecimal(res, precision, negative);
    }
}

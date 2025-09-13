using System;
using System.Text;

public static class HugeMath
{
    // --- Helpers ---
    private static (string digits, int scale, bool negative) Normalize(string s)
    {
        s = s.Trim();
        bool negative = s.StartsWith("-");
        if (negative) s = s.Substring(1);

        int scale = 0;
        if (s.Contains("."))
        {
            int dot = s.IndexOf(".");
            scale = s.Length - dot - 1;
            s = s.Remove(dot, 1);
        }

        // remove leading zeros
        s = s.TrimStart('0');
        if (s == "") s = "0";

        return (s, scale, negative);
    }

    private static string InsertDecimal(string digits, int scale, bool negative)
    {
        if (scale > 0)
        {
            if (digits.Length <= scale)
                digits = digits.PadLeft(scale + 1, '0');
            int pointPos = digits.Length - scale;
            digits = digits.Insert(pointPos, ".");
        }

        // clean trailing zeros
        if (digits.Contains("."))
        {
            digits = digits.TrimEnd('0');
            if (digits.EndsWith(".")) digits = digits.TrimEnd('.');
        }

        if (digits == "") digits = "0";
        if (negative && digits != "0") digits = "-" + digits;
        return digits;
    }

    // Compare absolute values (assumes no decimal point)
    private static int CompareAbs(string a, string b)
    {
        if (a.Length != b.Length) return a.Length.CompareTo(b.Length);
        return string.Compare(a, b, StringComparison.Ordinal);
    }

    // --- Addition (positive numbers only, decimals already aligned) ---
    private static string AddAbs(string a, string b)
    {
        StringBuilder sb = new();
        int carry = 0;

        a = a.PadLeft(Math.Max(a.Length, b.Length), '0');
        b = b.PadLeft(a.Length, '0');

        for (int i = a.Length - 1; i >= 0; i--)
        {
            int sum = (a[i] - '0') + (b[i] - '0') + carry;
            carry = sum / 10;
            sb.Insert(0, (char)('0' + (sum % 10)));
        }
        if (carry > 0) sb.Insert(0, (char)('0' + carry));
        return sb.ToString();
    }

    // --- Subtraction (|a| >= |b|, both positive) ---
    private static string SubAbs(string a, string b)
    {
        StringBuilder sb = new();
        int borrow = 0;

        a = a.PadLeft(Math.Max(a.Length, b.Length), '0');
        b = b.PadLeft(a.Length, '0');

        for (int i = a.Length - 1; i >= 0; i--)
        {
            int diff = (a[i] - '0') - (b[i] - '0') - borrow;
            if (diff < 0)
            {
                diff += 10;
                borrow = 1;
            }
            else borrow = 0;

            sb.Insert(0, (char)('0' + diff));
        }

        return sb.ToString().TrimStart('0').Length == 0 ? "0" : sb.ToString().TrimStart('0');
    }

    // --- Multiplication ---
    private static string MulAbs(string a, string b)
    {
        int[] result = new int[a.Length + b.Length];

        for (int i = a.Length - 1; i >= 0; i--)
        {
            for (int j = b.Length - 1; j >= 0; j--)
            {
                int mul = (a[i] - '0') * (b[j] - '0');
                int sum = result[i + j + 1] + mul;
                result[i + j + 1] = sum % 10;
                result[i + j] += sum / 10;
            }
        }

        StringBuilder sb = new();
        foreach (int num in result) if (!(sb.Length == 0 && num == 0)) sb.Append(num);
        return sb.Length == 0 ? "0" : sb.ToString();
    }

    // --- Division (integer part only, remainder dropped) ---
    private static string DivAbs(string a, string b, int precision)
    {
        if (b == "0") throw new DivideByZeroException();

        StringBuilder result = new();
        StringBuilder dividend = new();
        int idx = 0;

        // long division
        while (result.Length < precision + a.Length)
        {
            if (idx < a.Length) dividend.Append(a[idx++]);
            else dividend.Append('0');

            // remove leading zeros
            string current = dividend.ToString().TrimStart('0');
            if (current == "") current = "0";

            int count = 0;
            while (CompareAbs(current, b) >= 0)
            {
                current = SubAbs(current, b);
                count++;
            }

            result.Append(count);
            dividend.Clear();
            dividend.Append(current);
        }

        return result.ToString().TrimStart('0').Length == 0 ? "0" : result.ToString().TrimStart('0');
    }

    // --- Public Methods ---
    public static string Add(string a, string b)
    {
        var x = Normalize(a);
        var y = Normalize(b);

        int scale = Math.Max(x.scale, y.scale);
        x.digits = x.digits.PadRight(x.digits.Length + (scale - x.scale), '0');
        y.digits = y.digits.PadRight(y.digits.Length + (scale - y.scale), '0');

        string res;
        bool negative;

        if (x.negative == y.negative)
        {
            res = AddAbs(x.digits, y.digits);
            negative = x.negative;
        }
        else
        {
            int cmp = CompareAbs(x.digits, y.digits);
            if (cmp == 0) return "0";
            if (cmp > 0)
            {
                res = SubAbs(x.digits, y.digits);
                negative = x.negative;
            }
            else
            {
                res = SubAbs(y.digits, x.digits);
                negative = y.negative;
            }
        }

        return InsertDecimal(res, scale, negative);
    }

    public static string Subtract(string a, string b) =>
        Add(a, (b.StartsWith("-") ? b.Substring(1) : "-" + b));

    public static string Multiply(string a, string b)
    {
        var x = Normalize(a);
        var y = Normalize(b);

        string res = MulAbs(x.digits, y.digits);
        int scale = x.scale + y.scale;
        bool negative = x.negative ^ y.negative;

        return InsertDecimal(res, scale, negative);
    }

    public static string Divide(string a, string b, int precision = 50)
    {
        var x = Normalize(a);
        var y = Normalize(b);

        // align scales
        int scale = precision + Math.Max(x.scale, y.scale);
        x.digits = x.digits.PadRight(x.digits.Length + (scale - x.scale), '0');
        y.digits = y.digits.PadRight(y.digits.Length + (scale - y.scale), '0');

        string res = DivAbs(x.digits, y.digits, precision);
        bool negative = x.negative ^ y.negative;

        return InsertDecimal(res, precision, negative);
    }
}

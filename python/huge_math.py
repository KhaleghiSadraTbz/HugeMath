class HugeMath:
    # --- Helpers ---
    @staticmethod
    def Normalize(s):
        s = s.strip()
        negative = s.startswith("-")
        if negative:
            s = s[1:]

        scale = 0
        if "." in s:
            dot = s.index(".")
            scale = len(s) - dot - 1
            s = s[:dot] + s[dot+1:]

        s = s.lstrip("0")
        if s == "":
            s = "0"

        return (s, scale, negative)

    @staticmethod
    def InsertDecimal(digits, scale, negative):
        if scale > 0:
            if len(digits) <= scale:
                digits = digits.zfill(scale + 1)
            pointPos = len(digits) - scale
            digits = digits[:pointPos] + "." + digits[pointPos:]

        if "." in digits:
            digits = digits.rstrip("0")
            if digits.endswith("."):
                digits = digits[:-1]

        if digits == "":
            digits = "0"
        if negative and digits != "0":
            digits = "-" + digits
        return digits

    @staticmethod
    def CompareAbs(a, b):
        if len(a) != len(b):
            return (1 if len(a) > len(b) else -1)
        return (1 if a > b else -1 if a < b else 0)

    @staticmethod
    def AddAbs(a, b):
        carry = 0
        res = []

        max_len = max(len(a), len(b))
        a = a.zfill(max_len)
        b = b.zfill(max_len)

        for i in range(max_len - 1, -1, -1):
            s = (ord(a[i]) - ord('0')) + (ord(b[i]) - ord('0')) + carry
            carry = s // 10
            res.append(chr(ord('0') + (s % 10)))

        if carry > 0:
            res.append(chr(ord('0') + carry))

        return "".join(reversed(res))

    @staticmethod
    def SubAbs(a, b):
        res = []
        borrow = 0

        max_len = max(len(a), len(b))
        a = a.zfill(max_len)
        b = b.zfill(max_len)

        for i in range(max_len - 1, -1, -1):
            diff = (ord(a[i]) - ord('0')) - (ord(b[i]) - ord('0')) - borrow
            if diff < 0:
                diff += 10
                borrow = 1
            else:
                borrow = 0
            res.append(chr(ord('0') + diff))

        res_str = "".join(reversed(res)).lstrip("0")
        return "0" if res_str == "" else res_str

    @staticmethod
    def MulAbs(a, b):
        result = [0] * (len(a) + len(b))

        for i in range(len(a) - 1, -1, -1):
            for j in range(len(b) - 1, -1, -1):
                mul = (ord(a[i]) - ord('0')) * (ord(b[j]) - ord('0'))
                s = result[i + j + 1] + mul
                result[i + j + 1] = s % 10
                result[i + j] += s // 10

        sb = []
        for num in result:
            if not (len(sb) == 0 and num == 0):
                sb.append(str(num))

        return "0" if not sb else "".join(sb)

    @staticmethod
    def DivAbs(a, b, precision):
        if b == "0":
            raise ZeroDivisionError()

        result = []
        dividend = ""
        idx = 0

        while len(result) < precision + len(a):
            if idx < len(a):
                dividend += a[idx]
                idx += 1
            else:
                dividend += "0"

            current = dividend.lstrip("0")
            if current == "":
                current = "0"

            count = 0
            while HugeMath.CompareAbs(current, b) >= 0:
                current = HugeMath.SubAbs(current, b)
                count += 1

            result.append(str(count))
            dividend = current

        res_str = "".join(result).lstrip("0")
        return "0" if res_str == "" else res_str

    # --- Public Methods ---
    @staticmethod
    def Add(a, b):
        x = HugeMath.Normalize(a)
        y = HugeMath.Normalize(b)

        scale = max(x[1], y[1])
        x_digits = x[0] + "0" * (scale - x[1])
        y_digits = y[0] + "0" * (scale - y[1])

        if x[2] == y[2]:
            res = HugeMath.AddAbs(x_digits, y_digits)
            negative = x[2]
        else:
            cmp = HugeMath.CompareAbs(x_digits, y_digits)
            if cmp == 0:
                return "0"
            if cmp > 0:
                res = HugeMath.SubAbs(x_digits, y_digits)
                negative = x[2]
            else:
                res = HugeMath.SubAbs(y_digits, x_digits)
                negative = y[2]

        return HugeMath.InsertDecimal(res, scale, negative)

    @staticmethod
    def Subtract(a, b):
        return HugeMath.Add(a, (b[1:] if b.startswith("-") else "-" + b))

    @staticmethod
    def Multiply(a, b):
        x = HugeMath.Normalize(a)
        y = HugeMath.Normalize(b)

        res = HugeMath.MulAbs(x[0], y[0])
        scale = x[1] + y[1]
        negative = x[2] ^ y[2]

        return HugeMath.InsertDecimal(res, scale, negative)

    @staticmethod
    def Divide(a, b, precision=50):
        x = HugeMath.Normalize(a)
        y = HugeMath.Normalize(b)

        scale = precision + max(x[1], y[1])
        x_digits = x[0] + "0" * (scale - x[1])
        y_digits = y[0] + "0" * (scale - y[1])

        res = HugeMath.DivAbs(x_digits, y_digits, precision)
        negative = x[2] ^ y[2]

        return HugeMath.InsertDecimal(res, precision, negative)
              

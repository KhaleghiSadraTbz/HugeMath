# HugeMath

**HugeMath** is a pure string-based math library implemented in **C#**, **Java**, and **Python**, designed to handle arbitrarily large numbers with decimal precision — without relying on built-in big number libraries.

It provides basic arithmetic operations (`+`, `-`, `*`, `/`) with precision control, implemented in a way that mirrors manual long arithmetic.

---

## ✨ Features

* ✅ Arbitrary precision math using strings
* ✅ Supports addition, subtraction, multiplication, and division
* ✅ Handles negative numbers and decimals
* ✅ Deterministic results across C#, Java, and Python implementations
* ✅ Division with configurable precision

---

## 📂 Repository Structure

```
/HugeMath
 ├── csharp/
 │    └── HugeMath.cs
 ├── java/
 │    └── HugeMath.java
 ├── python/
 │    └── huge_math.py
 └── README.md
```

---

## 🚀 Usage

### C# Example

```csharp
using System;

class Program {
    static void Main() {
        Console.WriteLine(HugeMath.Add("123.45", "678.90"));   // 802.35
        Console.WriteLine(HugeMath.Subtract("1000", "1"));     // 999
        Console.WriteLine(HugeMath.Multiply("12.5", "4"));     // 50
        Console.WriteLine(HugeMath.Divide("10", "3", 20));     // 3.33333333333333333333
    }
}
```

### Java Example

```java
public class Main {
    public static void main(String[] args) {
        System.out.println(HugeMath.Add("123.45", "678.90"));   // 802.35
        System.out.println(HugeMath.Subtract("1000", "1"));     // 999
        System.out.println(HugeMath.Multiply("12.5", "4"));     // 50
        System.out.println(HugeMath.Divide("10", "3", 20));     // 3.33333333333333333333
    }
}
```

### Python Example

```python
from huge_math import HugeMath

print(HugeMath.Add("123.45", "678.90"))   # 802.35
print(HugeMath.Subtract("1000", "1"))     # 999
print(HugeMath.Multiply("12.5", "4"))     # 50
print(HugeMath.Divide("10", "3", 20))     # 3.33333333333333333333
```

---

## ⚖️ Methods

| Method                    | Description                                        |
| ------------------------- | -------------------------------------------------- |
| `Add(a, b)`               | Adds two numbers                                   |
| `Subtract(a, b)`          | Subtracts `b` from `a`                             |
| `Multiply(a, b)`          | Multiplies two numbers                             |
| `Divide(a, b, precision)` | Divides `a` by `b` with `precision` decimal places |

---

## 📜 License

GNU Affero General Public License

---

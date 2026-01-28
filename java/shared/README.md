# 🧮 Core Module

The `core` module is a high-performance, zero-dependency Java library dedicated to Fibonacci sequence computations. It
serves as the mathematical foundation for the entire ecosystem, providing multiple algorithmic implementations to handle
varying scales of numerical complexity.

---

## 🧬 Algorithmic Strategies

We provide three distinct strategies to balance computational speed and resource utilization:

### 1. Fast Doubling (Optimized)

* **Complexity**: $O(\log n)$
* **Mechanism**: Leverages matrix exponentiation identities to calculate $F_n$ in logarithmic time.
* **Best For**: Large-scale indices ($n > 1,000$) where linear time complexity becomes a bottleneck.

### 2. Iterative (Standard)

* **Complexity**: $O(n)$
* **Mechanism**: Uses a bottom-up dynamic programming approach with $O(1)$ space.
* **Best For**: Small to medium-sized indices where the overhead of fast doubling is unnecessary.

### 3. Naive Recursive (Reference)

* **Complexity**: $O(2^n)$
* **Mechanism**: Standard top-down recursion.
* **Caution**: Included for educational comparison; performance degrades exponentially and is unsuitable for $n > 40$.

---

## 🛠 Integration & Usage

The module is designed for simplicity and thread safety. All calculations return `java.math.BigInteger` to accommodate
the rapid growth of the Fibonacci sequence.

### Example Usage

```java
import com.github.mangila.fibonacci.core.FibonacciCalculator;

import java.math.BigInteger;

// Optimized calculation for large indices
BigInteger largeResult = FibonacciCalculator.fastDoubling(100000);

        // Standard iterative calculation
        BigInteger standardResult = FibonacciCalculator.iterative(500);
```

---

## 🏗 Module Structure

* **`com.github.mangila.fibonacci.core`**: Core logic and the `FibonacciCalculator` utility.
* **`com.github.mangila.fibonacci.core.entity`**: Shared domain objects (`FibonacciRequest`, `FibonacciResponse`)
  ensuring API consistency across modules.

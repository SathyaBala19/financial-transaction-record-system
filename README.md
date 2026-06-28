# Financial Transaction Record System (FTRS)

A Java-based financial transaction management system that handles deposits, withdrawals, transfers, and account summaries with full validation and error handling.

---

## Project Structure

```
src/
└── com/wipro/ftrs/
    ├── entity/         → Account, TransactionRecord
    ├── util/           → Custom Exceptions
    ├── service/        → Business Logic
    └── main/           → Entry Point
```

---

## Features

- Register and manage financial accounts
- Deposit, withdraw, and transfer funds
- Prevent overdrafts and invalid transactions
- View full transaction history per account
- Generate detailed account summary reports

---

## Technologies Used

- Java (JDK 8+)
- Object-Oriented Programming
- Custom Exception Handling
- ArrayList-based in-memory storage

---

## How to Run

```bash
# Compile
find src -name "*.java" | xargs javac -d out

# Run
java -cp out com.wipro.ftrs.main.Main
```

---

## Sample Output

```
Deposited 1500.0 to account A001. New Balance: 6500.0
Withdrew 500.0 from account A001. New Balance: 6000.0
Transferred 2000.0 from A001 to A002.

--- Transaction History for A001 ---
DEPOSIT - 1500.0 | Salary credit
WITHDRAW - 500.0 | ATM withdrawal
TRANSFER - 2000.0 | Transfer to A002
```

---

## Author
**Sathya Bala B** 

package com.wipro.ftrs.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import com.wipro.ftrs.entity.Account;
import com.wipro.ftrs.entity.TransactionRecord;
import com.wipro.ftrs.util.AccountNotFoundException;
import com.wipro.ftrs.util.InsufficientBalanceException;
import com.wipro.ftrs.util.InvalidTransactionException;

public class FinancialTransactionService {

    private ArrayList<Account> accounts;
    private ArrayList<TransactionRecord> transactions;

    public FinancialTransactionService(ArrayList<Account> accounts,
                                       ArrayList<TransactionRecord> transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public void addAccount(Account acc) {
        for (Account a : accounts) {
            if (a.getAccountId().equals(acc.getAccountId())) {
                System.out.println("Account already exists: " + acc.getAccountId());
                return;
            }
        }
        accounts.add(acc);
        System.out.println("Account added: " + acc.getAccountId() + " - " + acc.getAccountHolderName());
    }

    public Account findAccount(String accountId) throws AccountNotFoundException {
        for (Account a : accounts) {
            if (a.getAccountId().equals(accountId)) {
                return a;
            }
        }
        throw new AccountNotFoundException("Account not found: " + accountId);
    }

    public void deposit(String accountId, double amount, String remarks)
            throws AccountNotFoundException, InvalidTransactionException {

        Account account = findAccount(accountId);

        if (amount <= 0)
            throw new InvalidTransactionException("Deposit amount must be positive. Provided: " + amount);
        if (remarks == null || remarks.trim().isEmpty())
            throw new InvalidTransactionException("Remarks cannot be null or empty.");

        account.setBalance(account.getBalance() + amount);
        transactions.add(new TransactionRecord(
                generateId(), accountId, "DEPOSIT",
                amount, LocalDate.now().toString(), remarks));

        System.out.println("Deposited " + amount + " to " + accountId
                + ". New Balance: " + account.getBalance());
    }

    public void withdraw(String accountId, double amount, String remarks)
            throws AccountNotFoundException, InvalidTransactionException, InsufficientBalanceException {

        Account account = findAccount(accountId);

        if (amount <= 0)
            throw new InvalidTransactionException("Withdrawal amount must be positive. Provided: " + amount);
        if (remarks == null || remarks.trim().isEmpty())
            throw new InvalidTransactionException("Remarks cannot be null or empty.");
        if (account.getBalance() < amount)
            throw new InsufficientBalanceException("Insufficient balance in " + accountId
                    + ". Available: " + account.getBalance() + ", Requested: " + amount);

        account.setBalance(account.getBalance() - amount);
        transactions.add(new TransactionRecord(
                generateId(), accountId, "WITHDRAW",
                amount, LocalDate.now().toString(), remarks));

        System.out.println("Withdrew " + amount + " from " + accountId
                + ". New Balance: " + account.getBalance());
    }

    public void transfer(String fromId, String toId, double amount, String remarks)
            throws AccountNotFoundException, InvalidTransactionException, InsufficientBalanceException {

        Account fromAccount = findAccount(fromId);
        Account toAccount   = findAccount(toId);

        if (amount <= 0)
            throw new InvalidTransactionException("Transfer amount must be positive. Provided: " + amount);
        if (remarks == null || remarks.trim().isEmpty())
            throw new InvalidTransactionException("Remarks cannot be null or empty.");
        if (fromAccount.getBalance() < amount)
            throw new InsufficientBalanceException("Insufficient balance in " + fromId
                    + ". Available: " + fromAccount.getBalance() + ", Requested: " + amount);

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        transactions.add(new TransactionRecord(
                generateId(), fromId, "TRANSFER",
                amount, LocalDate.now().toString(), "Transfer to " + toId + ": " + remarks));

        toAccount.setBalance(toAccount.getBalance() + amount);
        transactions.add(new TransactionRecord(
                generateId(), toId, "TRANSFER",
                amount, LocalDate.now().toString(), "Transfer from " + fromId + ": " + remarks));

        System.out.println("Transferred " + amount + " from " + fromId + " to " + toId
                + ". Sender Balance: " + fromAccount.getBalance()
                + ", Receiver Balance: " + toAccount.getBalance());
    }

    public ArrayList<TransactionRecord> getTransactionHistory(String accountId)
            throws AccountNotFoundException {
        findAccount(accountId);
        ArrayList<TransactionRecord> history = new ArrayList<>();
        for (TransactionRecord tr : transactions) {
            if (tr.getAccountId().equals(accountId))
                history.add(tr);
        }
        return history;
    }

    public double calculateBalance(String accountId) throws AccountNotFoundException {
        return findAccount(accountId).getBalance();
    }

    public String generateAccountSummary(String accountId) {
        try {
            Account account = findAccount(accountId);
            ArrayList<TransactionRecord> history = getTransactionHistory(accountId);

            double totalDeposits = 0, totalWithdrawals = 0;
            TransactionRecord last = history.isEmpty() ? null : history.get(history.size() - 1);

            for (TransactionRecord tr : history) {
                if (tr.getType().equals("DEPOSIT"))       totalDeposits += tr.getAmount();
                else if (tr.getType().equals("WITHDRAW")) totalWithdrawals += tr.getAmount();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("ACCOUNT SUMMARY REPORT\n");
            sb.append("Account ID       : ").append(account.getAccountId()).append("\n");
            sb.append("Account Holder   : ").append(account.getAccountHolderName()).append("\n");
            sb.append("Current Balance  : ").append(account.getBalance()).append("\n");
            sb.append("Total Transactions: ").append(history.size()).append("\n");
            sb.append("Total Deposits   : ").append(totalDeposits).append("\n");
            sb.append("Total Withdrawals : ").append(totalWithdrawals).append("\n");
            if (last != null) {
                sb.append("Last Transaction : ").append(last.getType())
                  .append(" of ").append(last.getAmount())
                  .append(" on ").append(last.getDate())
                  .append(" (").append(last.getRemarks()).append(")\n");
            }
            sb.append("Financial Trend  : ")
              .append(totalDeposits >= totalWithdrawals ? "Positive" : "Negative").append("\n");
            return sb.toString();

        } catch (AccountNotFoundException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String generateId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
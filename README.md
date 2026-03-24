# 📱 My MoneyPal – Smart Personal Finance Manager

My MoneyPal is an Android application designed to help users manage their personal finances efficiently. It provides tools to track income and expenses, set budgets, and analyze financial data through intuitive visual reports.

---

## 🚀 Features

- 🔐 User Authentication (Login & Signup)
- 💰 Income Tracking with categories
- 💸 Expense Tracking with categories
- 📊 Visual Reports (Bar Charts & Pie Charts)
- 📈 Income vs Expenses Analysis
- 📅 Monthly Financial Summary
- 🎯 Budget Management
- 👤 User Profile & Settings
- 💾 Local Data Storage using SQLite

---

## 🛠️ Tech Stack

- **Language:** Java
- **Platform:** Android (Android Studio)
- **Database:** SQLite
- **UI Components:** Activities, Fragments
- **Architecture:** Multi-fragment navigation structure

---

## 📂 Project Structure

- `LoginActivity` – Handles user login
- `SignupActivity` – User registration
- `MainActivity` – Core app container
- `NavigationActivity` – Manages navigation between fragments
- `DataBaseHelper` – Handles SQLite database operations
- `SharedPrefManager` – Manages user session

### Fragments:
- `HomeFragment` – Dashboard overview
- `IncomeFragment` – Add/view income
- `ExpensesFragment` – Add/view expenses
- `BudgetFragment` – Budget planning
- `ReportFragment` – Financial reports
- `IncomeVsExpensesFragment` – Comparison analysis
- `MonthlyIncomeExpensesFragment` – Monthly summary
- `ExpensesByCategoryFragment` – Category breakdown
- `IncomeByCategoryFragment` – Income analysis

---

## 📊 Key Functionality

- Stores financial data locally using SQLite
- Categorizes transactions for better tracking
- Generates insights through visual charts
- Provides a clear overview of financial status


# 📱 My MoneyPal – Smart Personal Finance Manager


## 📌 Overview
This project is an Android-based personal finance management application that helps users track their income, expenses, and budgets while gaining insights through visual reports and analytics.

The app integrates authentication, local database management, financial tracking, and data visualization in a single system.

---

## ✨ Features

### 🔐 User Authentication
- Sign up with input validation (email, password rules)
- Login with "Remember Me" functionality
- Session management using SharedPreferences

---

### 💰 Income & Expense Tracking
- Add, update, and delete transactions
- Categorize transactions (Income / Expense)
- Store transaction details (amount, date, category, description)
- Organized display grouped by date

---

### 📂 Categories Management
- Create custom income and expense categories
- Edit and delete categories
- User-specific category storage

---

### 🎯 Budget Management
- Set budget limits per category
- Track spending against budget
- Alerts for:
  - ⚠ Half reached
  - ❗ Exceeded
- Monthly tracking system

---

### 📈 Data Visualization
- Income vs Expenses (Pie Chart)
- Expenses by Category (Bar Chart)
- Income by Category (Bar Chart)
- Monthly trends (Bar Chart)

---

### 🏠 Dashboard
- Displays:
  - Total income
  - Total expenses
  - Balance
- Filter data by:
  - Day / Week / Month / Custom range
- Navigate to reports and charts

---

### 📑 Reports
- Detailed transaction reports within selected period
- Grouped by date
- Income/Expense distinction

---

### ⚙️ Settings
- Dark mode toggle 🌙
- Period preference (Day / Week / Month)
- Manage categories

---

### 👤 Profile Management
- Update user details
- Change password
- Input validation

---

## 🗄️ Database Design

The app uses SQLite with the following tables:

- USER → stores user credentials  
- CATEGORY → user-specific categories  
- TRANSACTIONS → income & expense records  
- BUDGET → budget limits  
- PREFERENCES → user settings  

---

## 🏗️ Architecture

### Activities
- MainActivity → checks login state
- LoginActivity, SignupActivity

### Navigation
- NavigationActivity with Drawer Menu

### Fragments
- Home, Income, Expenses, Budget
- Reports & Charts
- Settings & Profile

### Helpers
- DataBaseHelper → SQLite operations
- SharedPrefManager → session management

---

## 🛠️ Technologies Used
- Java (Android)
- SQLite Database
- MPAndroidChart (Data Visualization)
- Android Fragments & Navigation Drawer
- SharedPreferences

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/your-repo-name.git
2. Open in Android Studio
Open the project folder
Allow Gradle to sync
3. Run the App
Use emulator or real device
Click Run ▶

🧪 Testing
Create a new account
Log in
Add income and expenses
Set budgets
View charts and reports
Try dark mode


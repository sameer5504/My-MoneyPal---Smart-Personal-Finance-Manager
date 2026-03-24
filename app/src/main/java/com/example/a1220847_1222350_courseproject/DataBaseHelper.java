package com.example.a1220847_1222350_courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(EMAIL TEXT PRIMARY KEY,FIRST_NAME TEXT, LAST_NAME TEXT,PASSWORD TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE CATEGORY(ID INTEGER PRIMARY KEY AUTOINCREMENT,USER_EMAIL TEXT,TYPE TEXT, NAME TEXT, FOREIGN KEY(USER_EMAIL) REFERENCES USER(EMAIL))");
        sqLiteDatabase.execSQL("CREATE TABLE TRANSACTIONS(ID INTEGER PRIMARY KEY AUTOINCREMENT,USER_EMAIL TEXT,TYPE TEXT, AMOUNT REAL,DATE TEXT,CATEGORY_ID INTEGER,DESCRIPTION TEXT, FOREIGN KEY(USER_EMAIL) REFERENCES USER(EMAIL), FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(ID))");
        sqLiteDatabase.execSQL("CREATE TABLE BUDGET(ID INTEGER PRIMARY KEY AUTOINCREMENT, USER_EMAIL TEXT, CATEGORY_ID INTEGER, BUDGET_LIMIT REAL, FOREIGN KEY(USER_EMAIL) REFERENCES USER(EMAIL), FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(ID))");
        sqLiteDatabase.execSQL("CREATE TABLE PREFERENCES(EMAIL TEXT PRIMARY KEY,MODE TEXT, PERIOD TEXT, FOREIGN KEY(EMAIL) REFERENCES USER(USER_EMAIL))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // User functions
    public void insertUser(User user) {  // Insert a user and add default categories
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // Insert user
        ContentValues cvUser = new ContentValues();
        cvUser.put("EMAIL", user.getEmail());
        cvUser.put("FIRST_NAME", user.getFirst_name());
        cvUser.put("LAST_NAME", user.getLast_name());
        cvUser.put("PASSWORD", user.getPassword());
        sqLiteDatabase.insert("USER", null, cvUser);
        ContentValues cvPref = new ContentValues();
        cvPref.put("EMAIL", user.getEmail());
        cvPref.put("MODE", "LIGHT"); // Default
        cvPref.put("PERIOD", "WEEK");
        sqLiteDatabase.insert("PREFERENCES", null, cvPref);
    }

    public void editUser(User user) {  // Edit a user
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FIRST_NAME", user.getFirst_name());
        contentValues.put("LAST_NAME", user.getLast_name());
        contentValues.put("PASSWORD", user.getPassword());
        String[] userEmail = new String[]{user.getEmail()};
        sqLiteDatabase.update("USER", contentValues, "EMAIL = ?", userEmail);
    }

    public Cursor getUserUsingEmail(String email) {  // Get a user using email
        String[] userEmail = new String[]{email};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = ?", userEmail);
    }

    // Category functions
    public void insertCategory(String userEmail, String name, String type) {  // Insert a category
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER_EMAIL", userEmail);
        contentValues.put("NAME", name);
        contentValues.put("TYPE", type);
        sqLiteDatabase.insert("CATEGORY", null, contentValues);
    }

    public Cursor getIncomeCategoriesByUser(String userEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] email = new String[]{userEmail};
        return sqLiteDatabase.rawQuery("SELECT ID, NAME FROM CATEGORY WHERE USER_EMAIL = ? AND TYPE = 'INCOME'", email);
    }

    public Cursor getExpenseCategoriesByUser(String userEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] email = new String[]{userEmail};
        return sqLiteDatabase.rawQuery("SELECT ID,NAME FROM CATEGORY WHERE USER_EMAIL = ? AND TYPE = 'EXPENSE'", email);
    }

    public void deleteCategory(int categoryId) {  // delete a category using its id
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] idStr = new String[]{String.valueOf(categoryId)};
        sqLiteDatabase.delete("CATEGORY", "ID = ?", idStr);
    }

    public void updateCategory(int categoryId, String name, String type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        cv.put("TYPE", type);
        db.update("CATEGORY", cv, "ID = ?", new String[]{String.valueOf(categoryId)});
    }


    // Transaction Functions
    public void insertTransaction(String userEmail, String type, double amount, String date, int categoryId, String description) {  // insert a transaction
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER_EMAIL", userEmail);
        contentValues.put("TYPE", type);
        contentValues.put("AMOUNT", amount);
        contentValues.put("DATE", date);
        contentValues.put("CATEGORY_ID", categoryId);
        contentValues.put("DESCRIPTION", description);
        sqLiteDatabase.insert("TRANSACTIONS", null, contentValues);
    }

    public Cursor getIncomeTransactionsByUser(String userEmail) {  // Get income transaction by user email sorted by date
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] email = new String[]{userEmail};
        return sqLiteDatabase.rawQuery("SELECT T.ID, T.AMOUNT, T.DATE, T.CATEGORY_ID, T.DESCRIPTION, C.NAME AS CATEGORY_NAME FROM TRANSACTIONS T LEFT JOIN CATEGORY C ON T.CATEGORY_ID = C.ID WHERE T.USER_EMAIL = ? AND T.TYPE = 'INCOME' ORDER BY T.DATE DESC", email);
    }
    public Cursor getExpenseTransactionsByUser(String userEmail) {  // Get expense transaction by user email sorted by date
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] email = new String[]{userEmail};
        return sqLiteDatabase.rawQuery("SELECT T.ID, T.AMOUNT, T.DATE, T.CATEGORY_ID, T.DESCRIPTION, C.NAME AS CATEGORY_NAME FROM TRANSACTIONS T LEFT JOIN CATEGORY C ON T.CATEGORY_ID = C.ID WHERE T.USER_EMAIL = ? AND T.TYPE = 'EXPENSE' ORDER BY T.DATE DESC", email);
    }
    public Cursor getTransactionsByUser(String userEmail,String start, String end) {  // Get transaction and its info and type by user email sorted by date
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT T.ID, T.AMOUNT, T.DATE, T.CATEGORY_ID, T.DESCRIPTION, C.NAME AS CATEGORY_NAME, T.TYPE FROM TRANSACTIONS T LEFT JOIN CATEGORY C ON T.CATEGORY_ID = C.ID WHERE T.USER_EMAIL = ? AND T.DATE BETWEEN ? AND ? ORDER BY T.DATE DESC", new String[]{userEmail, start, end});
    }

    public void deleteTransaction(int transactionId) {  // delete a transaction using its id
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] idStr = new String[]{String.valueOf(transactionId)};
        sqLiteDatabase.delete("TRANSACTIONS", "ID = ?", idStr);
    }

    public void editTransaction(int transactionId, String type, double amount, String date, int categoryId, String description) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TYPE", type);
        contentValues.put("AMOUNT", amount);
        contentValues.put("DATE", date);
        contentValues.put("CATEGORY_ID", categoryId);
        contentValues.put("DESCRIPTION", description);
        String[] idStr = new String[]{String.valueOf(transactionId)};
        sqLiteDatabase.update("TRANSACTIONS", contentValues, "ID = ?", idStr);
    }
    // Budget Functions

    public void updateInsertBudget(String userEmail, int categoryId, double limit) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT ID FROM BUDGET WHERE USER_EMAIL = ? AND CATEGORY_ID = ?",
                new String[]{userEmail, String.valueOf(categoryId)}
        );

        ContentValues cv = new ContentValues();
        cv.put("USER_EMAIL", userEmail);
        cv.put("CATEGORY_ID", categoryId);
        cv.put("BUDGET_LIMIT", limit);

        if (c.moveToFirst()) {
            int budgetId = c.getInt(0);
            db.update("BUDGET", cv, "ID = ?", new String[]{String.valueOf(budgetId)});
        } else {
            db.insert("BUDGET", null, cv);
        }
        c.close();
    }

    public Cursor getBudgetsWithCategoryNames(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT B.ID, B.CATEGORY_ID, C.NAME, B.BUDGET_LIMIT " +
                        "FROM BUDGET B " +
                        "JOIN CATEGORY C ON B.CATEGORY_ID = C.ID " +
                        "WHERE B.USER_EMAIL = ?",
                new String[]{userEmail}
        );
    }

    public double getMonthlySpentForCategory(
            String userEmail,
            int categoryId,
            String yearMonth // format: YYYY-MM
    ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(AMOUNT),0) " +
                        "FROM TRANSACTIONS " +
                        "WHERE USER_EMAIL = ? " +
                        "AND CATEGORY_ID = ? " +
                        "AND TYPE = 'EXPENSE' " +
                        "AND DATE LIKE ?",
                new String[]{userEmail, String.valueOf(categoryId), yearMonth + "%"}
        );

        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    public int getBudgetProgressPercent(
            String userEmail,
            int categoryId,
            double budgetLimit,
            String yearMonth
    ) {
        double spent = getMonthlySpentForCategory(userEmail, categoryId, yearMonth);
        if (budgetLimit == 0) return 0;
        return (int) ((spent / budgetLimit) * 100);
    }

    public String getBudgetAlertStatus(
            String userEmail,
            int categoryId,
            double budgetLimit,
            String yearMonth
    ) {
        double spent = getMonthlySpentForCategory(userEmail, categoryId, yearMonth);

        if (spent >= budgetLimit) {
            return "EXCEEDED";
        } else if (spent >= budgetLimit * 0.5) {
            return "HALF_REACHED";
        } else {
            return "OK";
        }
    }

    //Preferences
    public void setPreferences(String email, String mode, String period) {  // Insert or update preferences for a user
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EMAIL", email);
        cv.put("MODE", mode);
        cv.put("PERIOD", period);
        int rows = sqLiteDatabase.update("PREFERENCES", cv, "EMAIL = ?", new String[]{email});
        if (rows == 0) {  // If user has no preferences yet, insert new
            sqLiteDatabase.insert("PREFERENCES", null, cv);
        }
    }

    public void setDarkMode(String email, String mode) {  // Set only dark/light mode
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("MODE", mode);
        sqLiteDatabase.update("PREFERENCES", cv, "EMAIL = ?", new String[]{email});
    }

    public void setPeriod(String email, String period) {  // Set only period
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PERIOD", period);
        sqLiteDatabase.update("PREFERENCES", cv, "EMAIL = ?", new String[]{email});
    }

    public Cursor getPreferencesForUser(String email) {  // Get preferences for a user
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] args = new String[]{email};
        return sqLiteDatabase.rawQuery("SELECT * FROM PREFERENCES WHERE EMAIL = ?", args);
    }

    public void ensureDefaultCategories(String email) {  // Ensuring Defaults
        Cursor c = getIncomeCategoriesByUser(email);
        if (c.getCount() == 0) {
            insertCategory(email, "Salary", "INCOME");
            insertCategory(email, "Scholarship", "INCOME");
            insertCategory(email, "Bonus", "INCOME");
            insertCategory(email, "Food", "EXPENSE");
            insertCategory(email, "Rent", "EXPENSE");
            insertCategory(email, "Entertainment", "EXPENSE");
            insertCategory(email, "Transport", "EXPENSE");
        }
        c.close();
        Cursor cPref = getPreferencesForUser(email);
        if (cPref.getCount() == 0) {
            setPreferences(email, "DARK", "WEEK");  // Default
        } else {
            cPref.moveToFirst();
            String mode = cPref.getString(1);
            if (mode.equalsIgnoreCase("DARK")) {  // Set dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {  // Set Light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        cPref.close();
    }
//
public double getTotalAmount(String userEmail, String type, String startDate, String endDate) {
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.rawQuery(
            "SELECT IFNULL(SUM(AMOUNT), 0) " +
                    "FROM TRANSACTIONS " +
                    "WHERE USER_EMAIL = ? " +
                    "AND TYPE = ? " +
                    "AND DATE BETWEEN ? AND ?",
            new String[]{ userEmail, type, startDate, endDate }
    );
    double total = 0;
    if (c.moveToFirst()) {
        total = c.getDouble(0);
    }
    c.close();
    return total;
}
    public double getAllTimeTotal(String userEmail, String type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(AMOUNT), 0) " +
                        "FROM TRANSACTIONS " +
                        "WHERE USER_EMAIL = ? " +
                        "AND TYPE = ?",
                new String[]{ userEmail, type }
        );

        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    public List<CategorySum> getCategorySums(
            String userEmail,
            String type,
            String startDate,
            String endDate
    ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT IFNULL(C.NAME, 'Uncategorized') AS CAT, IFNULL(SUM(T.AMOUNT), 0) " +
                        "FROM TRANSACTIONS T " +
                        "LEFT JOIN CATEGORY C ON T.CATEGORY_ID = C.ID " +
                        "WHERE T.USER_EMAIL = ? " +
                        "AND T.TYPE = ? " +
                        "AND T.DATE BETWEEN ? AND ? " +
                        "GROUP BY CAT " +
                        "ORDER BY SUM(T.AMOUNT) DESC",
                new String[]{ userEmail, type, startDate, endDate }
        );

        List<CategorySum> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(new CategorySum(c.getString(0), c.getDouble(1)));
        }
        c.close();
        return list;
    }
    public static class MonthlySum {
        public final String label;
        public final double total;

        public MonthlySum(String label, double total) {
            this.label = label;
            this.total = total;
        }
    }
    public static class CategorySum {
        public final String category;
        public final double total;

        public CategorySum(String category, double total) {
            this.category = category;
            this.total = total;
        }
    }
    public List<MonthlySum> getMonthlyExpenses(String userEmail, int months) {
        List<MonthlySum> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Calendar now = Calendar.getInstance();

        for (int i = months - 1; i >= 0; i--) {
            Calendar start = (Calendar) now.clone();
            start.add(Calendar.MONTH, -i);
            start.set(Calendar.DAY_OF_MONTH, 1);

            Calendar end = (Calendar) start.clone();
            end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

            String startDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, 1);

            String endDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    end.get(Calendar.YEAR), end.get(Calendar.MONTH) + 1, end.getActualMaximum(Calendar.DAY_OF_MONTH));

            // SQL query to sum expenses for the month directly
            Cursor c = db.rawQuery(
                    "SELECT IFNULL(SUM(AMOUNT),0) " +
                            "FROM TRANSACTIONS " +
                            "WHERE USER_EMAIL = ? " +
                            "AND TYPE = 'EXPENSE' " +
                            "AND DATE BETWEEN ? AND ?",
                    new String[]{userEmail, startDate, endDate}
            );

            double total = 0;
            if (c.moveToFirst()) {
                total = c.getDouble(0);
            }
            c.close();

            String label = new java.text.SimpleDateFormat("MMM yyyy", Locale.getDefault())
                    .format(start.getTime());

            list.add(new MonthlySum(label, total));
        }

        return list;
    }
}
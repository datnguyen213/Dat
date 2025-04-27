package com.example.currencyconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CurrencyConverter.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "conversion_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_FROM_CURRENCY = "from_currency";
    private static final String COLUMN_TO_CURRENCY = "to_currency";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_RATE = "rate";

    public CurrencyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_FROM_CURRENCY + " TEXT,"
                + COLUMN_TO_CURRENCY + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_RESULT + " REAL,"
                + COLUMN_RATE + " REAL"
                + ")";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addConversion(ConversionHistory history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, history.getDate());
        values.put(COLUMN_FROM_CURRENCY, history.getFromCurrency());
        values.put(COLUMN_TO_CURRENCY, history.getToCurrency());
        values.put(COLUMN_AMOUNT, history.getAmount());
        values.put(COLUMN_RESULT, history.getResult());
        values.put(COLUMN_RATE, history.getRate());

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public List<ConversionHistory> getAllConversions() {
        List<ConversionHistory> historyList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ConversionHistory history = new ConversionHistory(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6));
                historyList.add(history);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }
}
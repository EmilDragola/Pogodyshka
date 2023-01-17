package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WeatherDB extends SQLiteOpenHelper {

    private static final String db_name = "WeatherDB";
    private static final int db_ver = 1;
    private static final String db_table_1 = "current_weather";
    private static final String column_cur_city = "City";
    private static final String column_cur_weather = "Weather";
    private static final String column_cur_temp_like = "FeelsLike";
    private static final String column_cur_icon = "Icon";
    private static final String column_cur_vlazh = "Vlazh";
    private static final String column_cur_speed = "Speed";
    private static final String db_table_2 = "predict_weather";
    private static final String column_predict_icon = "Icon";
    private static final String column_predict_date = "Date";
    private static final String column_predict_temp = "Temp";
    private static final String db_column_2 = "WeatherDB";

//    База данных: WeatherDB
//    Таблица 1: current_weather
//    Общий вид:
//    ID   City     Weather   Icon    FeelsLike    Vlazh   Speed
//    1    Moscow    -5       n40     -4           20      10

//    Таблица 2: current_forecast
//    Общий вид:
//    ID    Icon    Date    Temp
//    1     n40     25      -3


    // Процедура-конструктор базы данных
    public WeatherDB(@Nullable Context context) {
        super(context, db_name, null, db_ver);
    }

    // В теле данного метода onCreate реадизуем sql-запрос для создания таблиц
    //  в уже созданной на предыдущем шаге базе данных
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // В переменной query записан sql-запрос, для создания таблицы с тремя столбцами (структуру таблицы см. выше), для удобства используется форматированная строка (чтобы не писать названия столбцов вручную в строку),
        // %s позиционно соответствуют значения переменных, перечисленных через запятую
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL);", db_table_1, column_cur_city, column_cur_weather, column_cur_icon, column_cur_temp_like, column_cur_vlazh, column_cur_speed);
        sqLiteDatabase.execSQL(query); // выполняем запрос, записанный в query
        String query1 = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL);", db_table_2, column_predict_icon, column_predict_date, column_predict_temp);
        sqLiteDatabase.execSQL(query1);
    }

    // В теле метода onUpgrade реализуем перезапись значений в базе данных,
    // используя соответствующий sql-запрос для удаления существующей таблицы.
    // Затем, вызываем метод onCreate(), для создания пустой таблицы
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = String.format("DELETE TABLE IF EXISTS %s", db_table_1);
        sqLiteDatabase.execSQL(query); // выполняем запрос, записанный в query
        onCreate(sqLiteDatabase); // Создаём пустую таблицу
    }

    public void deleteAll() // Удаляет все элементы из таблицы
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ db_table_1);
        db.close();
    }

    public void deleteAll1() // Удаляет все элементы из таблицы
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ db_table_2);
        db.close();
    }

    // Реализуем метод для добавления данных в таблицу "current_weather"
    public void insertData(String city, String weather, String icon, String temp_like, String vlazh, String speed){
        deleteAll();
        SQLiteDatabase db = this.getWritableDatabase(); // Создаём абстракцию для записи данных в БД
        ContentValues values = new ContentValues(); // Создаем объекс класса ContentValues для записи значений в таблицу, используя соответствующие методы этого класса
        values.put(column_cur_city, city); // В столбец column_cur_city записывается город, переданный в строковом параметре city,
        // далее по аналогии остальные столбцы заполняется данными
        values.put(column_cur_weather, weather);
        values.put(column_cur_icon, icon);
        values.put(column_cur_temp_like, temp_like);
        values.put(column_cur_vlazh, vlazh);
        values.put(column_cur_speed, speed);
        // Добавляем заполненные столбцы в таблицу db_table_1
        db.insertWithOnConflict(db_table_1, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void insertData2(String icon, String date, String temp){
        deleteAll1();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column_predict_icon, icon);
        values.put(column_predict_date, date);
        values.put(column_predict_temp, temp);
        db.insertWithOnConflict(db_table_2, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Реализуем метод для получения данных из таблицы "current_weather" в виде строкового динамического массива
    public ArrayList<String> get_cur_data(){
        ArrayList<String> cur_data = new ArrayList<>(); // создаем пустой список (инициализируем параметр cur_data)
        SQLiteDatabase db = this.getReadableDatabase(); // Создаём абстракцию для считывания данных из БД
        // создаём объект класса Cursor - по сути это итератор, считывающий поэлементно данные в таблице, затем эти данные добавляются в строковый массив, параметры выборки и группировки инициализируем как null
        Cursor cursor = db.query(db_table_1, new String[]{column_cur_city, column_cur_weather, column_cur_icon, column_cur_temp_like, column_cur_vlazh,column_cur_speed }, null, null, null, null, null);
        while (cursor.moveToNext()){
            // Перебираем значения в таблице и последовательно добавляем их в массив
            int index = cursor.getColumnIndex(column_cur_city);
            cur_data.add(cursor.getString(index));

            int i = cursor.getColumnIndex(column_cur_weather);
            cur_data.add(cursor.getString(i));

            int j = cursor.getColumnIndex(column_cur_icon);
            cur_data.add(cursor.getString(j));

            int index2 = cursor.getColumnIndex(column_cur_temp_like);
            cur_data.add(cursor.getString(index2));

            int index3 = cursor.getColumnIndex(column_cur_vlazh);
            cur_data.add(cursor.getString(index3));

            int index4 = cursor.getColumnIndex(column_cur_speed);
            cur_data.add(cursor.getString(index4));

        }
        cursor.close(); // Заканчиваем работу с курсором
        db.close(); // Заканчиваем работу с БД
        return cur_data; // Возвращаем список значений
    }

    public ArrayList<String> get_cur_data1(){
        ArrayList<String> cur_data = new ArrayList<>(); // создаем пустой список (инициализируем параметр cur_data)
        SQLiteDatabase db = this.getReadableDatabase(); // Создаём абстракцию для считывания данных из БД
        // создаём объект класса Cursor - по сути это итератор, считывающий поэлементно данные в таблице, затем эти данные добавляются в строковый массив, параметры выборки и группировки инициализируем как null
        Cursor cursor = db.query(db_table_2, new String[]{column_predict_icon, column_predict_date, column_predict_temp}, null, null, null, null, null);
        while (cursor.moveToNext()){
            // Перебираем значения в таблице и последовательно добавляем их в массив
            int index = cursor.getColumnIndex(column_predict_icon);
            cur_data.add(cursor.getString(index));

            int i = cursor.getColumnIndex(column_predict_date);
            cur_data.add(cursor.getString(i));

            int j = cursor.getColumnIndex(column_predict_temp);
            cur_data.add(cursor.getString(j));
        }
        cursor.close(); // Заканчиваем работу с курсором
        db.close(); // Заканчиваем работу с БД
        return cur_data; // Возвращаем список значений
    }
}

/**
 * Author: Adee
 * facebook: http://facebook.com/ideal.adee
 **/
package com.taxiappclone.common.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import static com.taxiappclone.common.app.AppConfig.LOCAL_DB_NAME;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	//Table names
	public static final String TABLE_LOCATIONS = "stored_locations";
	public static final String TABLE_CREDIT_CARDS = "credit_cards";

	private Context context;

	public SQLiteHandler(Context context) {
		super(context, LOCAL_DB_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public SQLiteHandler(Context context, int dbVersion) {
		super(context, LOCAL_DB_NAME, null, dbVersion);
		this.context = context;
	}
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
				+ "id" + " INTEGER PRIMARY KEY,"
				+ "server_id" + " INTEGER,"
				+ "address_type" + " TEXT,"
				+ "name" + " TEXT,"
				+ "address" + " TEXT,"
				+ "city"+" TEXT,"
				+ "state"+" TEXT,"
				+ "country"+" TEXT,"
				+ "country_code"+" TEXT,"
				+ "postal_code"+" TEXT,"
				+ "place_id"+" TEXT,"
				+ "latitude" + " DOUBLE,"
				+ "longitude" + " DOUBLE,"
				+ "created" + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
		db.execSQL(CREATE_LOCATIONS_TABLE);
		String CREATE_CREDIT_CARDS_TABLE = "CREATE TABLE " + TABLE_CREDIT_CARDS + "("
				+ "id" + " INTEGER PRIMARY KEY,"
				+ "server_id" + " INTEGER,"
				+ "card_holder_name" + " TEXT,"
				+ "card_network" + " TEXT,"
				+ "card_first_digit" + " TEXT,"
				+ "card_last_digits"+" TEXT,"
				+ "billing_address"+" TEXT,"
				+ "expires_at"+" TEXT,"
				+ "created" + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
		db.execSQL(CREATE_CREDIT_CARDS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDIT_CARDS);
		// Create tables again
		onCreate(db);
	}
	/**
	 * Credit Card Functions
	 */
	public void addCreditCard(ContentValues values)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		// Inserting Row
		long id = db.insert(TABLE_CREDIT_CARDS, null, values);
		db.close(); // Closing database connection
		Log.d(TAG, "New Location inserted into sqlite: " + id);
	}

	public void updateCreditCard(int id , ContentValues values)
	{
		SQLiteDatabase DB = this.getWritableDatabase();
		DB.update(TABLE_CREDIT_CARDS, values, "server_id=?", new String[]{String.valueOf(id)});
	}

	public Cursor getCreditCards(int offset)
	{
		String countQuery = "SELECT * FROM "+TABLE_CREDIT_CARDS+" ORDER BY id ASC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		return cursor;
	}

	public Boolean deleteCreditCard(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_CREDIT_CARDS, "server_id="+id, null) > 0;
	}

	public boolean checkCreditCardExists(int serverId) {
		String countQuery = "SELECT * FROM "+TABLE_CREDIT_CARDS+" WHERE server_id="+serverId+" ORDER BY id DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		return rowCount>0;
	}

	/**
	 * Stored Location Functions
	 */
	public void addLocation(ContentValues values)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		// Inserting Row
		long id = db.insert(	TABLE_LOCATIONS, null, values);
		db.close(); // Closing database connection
		Log.d(TAG, "New Location inserted into sqlite: " + id);
	}

	public void updateLocation(int id , ContentValues values)
	{
		SQLiteDatabase DB = this.getWritableDatabase();
		DB.update(TABLE_LOCATIONS, values, "server_id=?", new String[]{String.valueOf(id)});
	}

	public Cursor getStoredLocations(int offset)
	{
		String countQuery = "SELECT * FROM "+TABLE_LOCATIONS+" ORDER BY id DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		return cursor;
	}

	public Boolean deleteLocation(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_LOCATIONS, "server_id="+id, null) > 0;
	}

	public boolean checkLocationExists(int serverId) {
		String countQuery = "SELECT * FROM "+TABLE_LOCATIONS+" WHERE server_id="+serverId+" ORDER BY id DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		return rowCount>0;
	}

	public void showToast(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteDatabse() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
		context.deleteDatabase(LOCAL_DB_NAME);
		db.close();
		Log.d(TAG, "Deleted all user info from sqlite");
	}
}
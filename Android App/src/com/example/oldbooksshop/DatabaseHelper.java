package com.example.oldbooksshop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	  public static final String DATABASE_NAME = "OLDBOOKS_SHOP.db";
	  public static final int DATABASE_VERSION = 1;
	  public static final String TABLE_USER_LOGIN="USER_CREDENTIALS";
	  public static final String TABLE_CACHED_CATEGORIES = "CACHED_CATEGORIES";
	  public static final String TABLE_CACHED_MYBOOKS = " CACHED_MY_BOOKS";
	  public static final String TABLE_CACHED_BIDDERS = " CACHED_BIDDERS";
	  public static final String TABLE_CACHED_BOOKSCATEGORYWISE = " CACHED_BOOKS_CATEGORYWISE";
	  public static final String BOOK_CATEGORY = "category";
	  public static final String CACHED_BOOK_ID = "_id";
	  public static final String BOOK_ID = "book_id";
	  public static final String BOOK_NAME = "name";
	  public static final String BOOK_AUTHOR="author";
	  public static final String BOOK_EDITION = "edition";
	  public static final String BOOK_PUBLISHER="publisher";
	  public static final String BOOK_IMAGE="image";
	  public static final String BOOK_MSP="msp";
	  public static final String BOOK_COMMENTS="comments";
	  public static final String BID_ID = "_id";
	  public static final String BID_SHOPPER_ID="shoppers_id";
	  public static final String BID_SHOPPER_PHONE_NUM = "shopper_phone_num";
	  public static final String BID_QUOTED_PRICE="quoted_price";
	  public static final String BID_BOOK_ID="bidded_book";
	  public static final String MY_BOOKS_ID="_id";
	  public static final String MYBOOK_NAME = "my_books_name";
	  public static final String MYBOOK_AUTHOR="my_books_author";
	  public static final String MYBOOK_MSP="my_books_msp";
	  public static final String MYBOOK_EDITION="my_books_edition";
	  public static final String MYBOOK_PUBLISHER="my_books_publisher";
	  public static final String MYBOOK_ID="my_books_id";
	  public static final String MYBOOK_IMAGE="my_books_image";
	  public static final String MYBOOK_VALIDTILL="my_book_valid_till";
	  public static final String LOGIN_ID="_id";
	  public static final String PASSWORD="Password";
	  public static final String CATEGORY_ID = "_id";
	  public static final String CATEGORY_NAME = "CATEGORY";
	  final static String[] columns = { LOGIN_ID, PASSWORD };
	  final static String[] category_columns = { CATEGORY_ID, CATEGORY_NAME };
	  private static int count = 0;
	  private static int bidder_count=0;
	  private static int cached_book_count=0;
	  String sqlQ;
	  private Context mContext;
	  
	  public DatabaseHelper(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
           mContext = context;
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {
		sqlQ = "CREATE TABLE " + TABLE_USER_LOGIN + "( " + LOGIN_ID + " text primary key unique," + PASSWORD + " text not null )";
        db.execSQL(sqlQ);
        
        sqlQ = "CREATE TABLE " + TABLE_CACHED_CATEGORIES + "( " + CATEGORY_ID + " text primary key unique," + CATEGORY_NAME + " text not null )";
        db.execSQL(sqlQ);
        
        sqlQ = "CREATE TABLE " + TABLE_CACHED_MYBOOKS + "(" + MY_BOOKS_ID + " text primary key unique," + MYBOOK_NAME + " text not null, " + MYBOOK_AUTHOR + " text not null, " + MYBOOK_MSP + " text not null, " + MYBOOK_EDITION + " text not null, " + MYBOOK_PUBLISHER + " text not null, " + MYBOOK_ID + " text not null, " + MYBOOK_IMAGE + " text not null, " + MYBOOK_VALIDTILL + " text not null)"; 
        db.execSQL(sqlQ);
        
        sqlQ = "CREATE TABLE " + TABLE_CACHED_BIDDERS + "( " + BID_ID + " text primary key unique," + BID_SHOPPER_ID + " text not null, " + BID_BOOK_ID + " text not null, " + BID_QUOTED_PRICE + " text not null, " + BID_SHOPPER_PHONE_NUM + " text not null)";
        db.execSQL(sqlQ);
        
        sqlQ = "CREATE TABLE " + TABLE_CACHED_BOOKSCATEGORYWISE + "( " + CACHED_BOOK_ID + " text primary key unique, "  + BOOK_ID + " text not null, " + BOOK_CATEGORY + " text not null, "+ BOOK_NAME + " text not null, " + BOOK_AUTHOR + " text not null, " + BOOK_MSP + " text not null, " + BOOK_EDITION + " text not null, " + BOOK_PUBLISHER + " text not null, " + BOOK_IMAGE + " text not null, " + BOOK_COMMENTS + " text not null)";
        db.execSQL(sqlQ);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	
		
	}
	
	void deleteDatabase() {
		mContext.deleteDatabase(DATABASE_NAME);
	}
	
	public boolean checkIfBooksCachedForCategory(String category){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "SELECT COUNT(*) FROM " + TABLE_CACHED_BOOKSCATEGORYWISE + " WHERE " + BOOK_CATEGORY + "=\'" + category + "\'";
			Log.i("Database Helper","checking for cached books");
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			if(c.getInt(0) == 0){
				Log.i("DatabaseHelper", "cached category wise books table empty");
				return false;
			}
			else{
				Log.i("DatabaseHelper", "cached category wise books table contains data");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String getBooksForCategory(String category){
		
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT * FROM " + TABLE_CACHED_BOOKSCATEGORYWISE + " where " + BOOK_CATEGORY + " = \'" + category + "\'";
		try{
			StringBuilder temp = new StringBuilder();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of cached books. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "book " + c.getString(c.getColumnIndex(BOOK_NAME)));
				temp.append(c.getString(c.getColumnIndex(BOOK_ID)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_NAME)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_AUTHOR)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_MSP)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_EDITION)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_PUBLISHER)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_IMAGE)));
				temp.append("`");
				temp.append(c.getString(c.getColumnIndex(BOOK_COMMENTS)));
				temp.append("|");
			}
			Log.i("DatabaseHelper","cached books by category obtained. returning.");
			return temp.toString();
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getBooksForCategory returning null");
			return null;
		
		}
	}
	
	
	public boolean cacheBooksForCategory(String book_id, String name, String author, String msp, String edition, String publisher, String image, String category, String comments){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			Log.i("Database Helper","caching book by category");
			cv.put(CACHED_BOOK_ID, cached_book_count++);
			cv.put(BOOK_ID, book_id);
			cv.put(BOOK_CATEGORY, category);
			cv.put(BOOK_NAME, name);
			cv.put(BOOK_AUTHOR, author);
			cv.put(BOOK_MSP, msp);
			cv.put(BOOK_EDITION, edition);
			cv.put(BOOK_PUBLISHER, publisher);
			cv.put(BOOK_IMAGE, image);
			cv.put(BOOK_COMMENTS, comments);
			
			database.insert(TABLE_CACHED_BOOKSCATEGORYWISE, null, cv);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteCachedBooksByCategory(){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_CACHED_BOOKSCATEGORYWISE;
			database.execSQL(sqlQ);
			cached_book_count = 0;
			Log.i("DatabaseHelper", "Cleared cached booksByCategory");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	
	public boolean cacheBidder(String shopper_id, int book_id, int quoted_price, int phone_number){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			//sqlQ = "DELETE FROM " + TABLE_CACHED_MYBOOKS;
			//database.execSQL(sqlQ);
			ContentValues cv = new ContentValues();
			Log.i("Database Helper","caching bidder");
			cv.put(BID_ID, bidder_count++);
			cv.put(BID_SHOPPER_ID, shopper_id);
			cv.put(BID_BOOK_ID, new Integer(book_id).toString());
			cv.put(BID_QUOTED_PRICE, new Integer(quoted_price).toString());
			cv.put(BID_SHOPPER_PHONE_NUM, phone_number);
			database.insert(TABLE_CACHED_BIDDERS, null, cv);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean clearCachedBidders(){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_CACHED_BIDDERS;
			database.execSQL(sqlQ);
			bidder_count = 0;
			Log.i("DatabaseHelper", "Cleared cached bidders");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean cacheCategories(List<String> categories){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_CACHED_CATEGORIES;
			database.execSQL(sqlQ);
			int count=0;
			ContentValues cv = new ContentValues();
			Iterator<String> it = categories.iterator();
			while(it.hasNext()){
				String temp = it.next().toString();
				Log.i("DatabaseHelper", "cacheCategories: count is " + count + " category is: " + temp);
				cv.put(CATEGORY_ID, count++);
				cv.put(CATEGORY_NAME, temp);
				database.insert(TABLE_CACHED_CATEGORIES, null, cv);
			}
			Log.i("DatabaseHelper","cached categories");
			return true;
		}catch(Exception e){
			Log.i("DatabaseHelper", "Error caching categories");
			return false;
		}
		
	}
	
	public ArrayList<String> getShopperidsForBook(int book_id){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + BID_SHOPPER_ID + " FROM " + TABLE_CACHED_BIDDERS + " where " + BID_BOOK_ID + " = " + new Integer(book_id).toString();
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of cached shoppers for book. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding shopper: " + c.getString(c.getColumnIndex(BID_SHOPPER_ID)));
				temp.add(c.getString(c.getColumnIndex(BID_SHOPPER_ID)));
				
			}
			Log.i("DatabaseHelper","cached shopper ids obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getShopperidsForBook returning null");
			return null;
		
		}
	}
	
	public ArrayList<Integer> getShoppersprice(int book_id){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + BID_QUOTED_PRICE + " FROM " + TABLE_CACHED_BIDDERS + " where " + BID_BOOK_ID + " = " + new Integer(book_id).toString();
		try{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of shopper quotes. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding price: " + c.getString(c.getColumnIndex(BID_QUOTED_PRICE)));
				temp.add(Integer.parseInt(c.getString(c.getColumnIndex(BID_QUOTED_PRICE))));
				
			}
			Log.i("DatabaseHelper","shopper quoted prices obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getShopperPrices returning null");
			return null;
		
		}
	}
	

	public ArrayList<Integer> getShopperPhoneNumberForBook(int book_id){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + BID_SHOPPER_PHONE_NUM + " FROM " + TABLE_CACHED_BIDDERS + " where " + BID_BOOK_ID + " = " + new Integer(book_id).toString();
		try{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of shopper phone numbers. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding ph no.: " + c.getString(c.getColumnIndex(BID_SHOPPER_PHONE_NUM)));
				temp.add(Integer.parseInt(c.getString(c.getColumnIndex(BID_SHOPPER_PHONE_NUM))));
				
			}
			Log.i("DatabaseHelper","shopper ph no obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getShopperPhno returning null");
			return null;
		
		}
	}
	
	public boolean checkIfBidderCachedForBook(int book_id){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT COUNT(*) FROM " + TABLE_CACHED_BIDDERS + " where " + BID_BOOK_ID + " = " + new Integer(book_id).toString();
		try{
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			if(c.getInt(0) == 0){
				Log.i("DatabaseHelper", "cache bidders table empty for book");
				return false;
			}
			else{
				Log.i("DatabaseHelper", "cache bidders contains data for book");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean checkIfCategoriesCached(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT COUNT(*) FROM " + TABLE_CACHED_CATEGORIES;
		try{
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			if(c.getInt(0) == 0){
				Log.i("DatabaseHelper", "cache categories table empty");
				return false;
			}
			else{
				Log.i("DatabaseHelper", "cache categories contains data");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getCategories(){
		
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + CATEGORY_NAME + " FROM " + TABLE_CACHED_CATEGORIES;
		try{
			List<String> categories = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of categories. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding category: " + c.getString(c.getColumnIndex(CATEGORY_NAME)));
				categories.add(c.getString(c.getColumnIndex(CATEGORY_NAME)));
				
			}
			Log.i("DatabaseHelper","categories obtained. returning.");
			return categories;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","returning null");
			return null;
		
		}
	}
	
	public boolean checkIfMyBooksCached(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT COUNT(*) FROM " + TABLE_CACHED_MYBOOKS;
		try{
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			if(c.getInt(0) == 0){
				Log.i("DatabaseHelper", "my books table empty");
				return false;
			}
			else{
				Log.i("DatabaseHelper", "my books table contains data");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addMyBooksToDatabase(String book_name, String book_author, int book_msp, String book_edition, String book_publisher, int book_id, String image, String book_validTill){
		try{
			
			SQLiteDatabase database = this.getWritableDatabase();
			//sqlQ = "DELETE FROM " + TABLE_CACHED_MYBOOKS;
			//database.execSQL(sqlQ);
			ContentValues cv = new ContentValues();
			Log.i("Database Helper","caching my books");
			cv.put(MY_BOOKS_ID, count++);
			cv.put(MYBOOK_NAME, book_name);
			cv.put(MYBOOK_AUTHOR, book_author);
			cv.put(MYBOOK_MSP, new Integer(book_msp).toString());
			cv.put(MYBOOK_EDITION, book_edition);
			cv.put(MYBOOK_PUBLISHER, book_publisher);
			cv.put(MYBOOK_ID,new Integer(book_id).toString());
			cv.put(MYBOOK_IMAGE, image);
			cv.put(MYBOOK_VALIDTILL,book_validTill);
			database.insert(TABLE_CACHED_MYBOOKS, null, cv);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean removeMyBook(int book_id){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_CACHED_MYBOOKS + " where " + MY_BOOKS_ID + "='" + book_id + "'";
			database.execSQL(sqlQ);
			count = count - 1;
			Log.i("DatabaseHelper", "Cleared my book id" + book_id);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean clearCachedMyBooks(){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_CACHED_MYBOOKS;
			database.execSQL(sqlQ);
			count = 0;
			Log.i("DatabaseHelper", "Cleared cached my books");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<String> getMyBooksAuthors(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_AUTHOR + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book authors. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding author: " + c.getString(c.getColumnIndex(MYBOOK_AUTHOR)));
				temp.add(c.getString(c.getColumnIndex(MYBOOK_AUTHOR)));
				
			}
			Log.i("DatabaseHelper","my book authors obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookauthors returning null");
			return null;
		
		}
	}
	
	public ArrayList<String> getMyBooksValidTill(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_VALIDTILL + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book valid till. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding valid till: " + c.getString(c.getColumnIndex(MYBOOK_VALIDTILL)));
				temp.add(c.getString(c.getColumnIndex(MYBOOK_VALIDTILL)));
				
			}
			Log.i("DatabaseHelper","my book valid till obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookvalidtill returning null");
			return null;
		
		}
	}
	
	public ArrayList<String> getMyBooksImage(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_IMAGE + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book images. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding image: ");
				temp.add(c.getString(c.getColumnIndex(MYBOOK_IMAGE)));
				
			}
			Log.i("DatabaseHelper","my book images obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookimages returning null");
			return null;
		
		}
	}
	
	public ArrayList<String> getMyBooksEdition(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_EDITION + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book editions. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding edition: " + c.getString(c.getColumnIndex(MYBOOK_EDITION)));
				temp.add(c.getString(c.getColumnIndex(MYBOOK_EDITION)));
				
			}
			Log.i("DatabaseHelper","my book editions obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookedition returning null");
			return null;
		
		}
	}
	
	public ArrayList<String> getMyBooksPublishers(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_PUBLISHER + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book publishers. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding publisher: " + c.getString(c.getColumnIndex(MYBOOK_PUBLISHER)));
				temp.add(c.getString(c.getColumnIndex(MYBOOK_PUBLISHER)));
				
			}
			Log.i("DatabaseHelper","my book publishers obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookpublisher returning null");
			return null;
		
		}
	}
	
	public ArrayList<Integer> getMyBooksMsp(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_MSP + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book msp. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding msp: " + c.getString(c.getColumnIndex(MYBOOK_MSP)));
				temp.add(Integer.parseInt(c.getString(c.getColumnIndex(MYBOOK_MSP))));
				
			}
			Log.i("DatabaseHelper","my book msps obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookmsps returning null");
			return null;
		
		}
	}
	
	public ArrayList<Integer> getMyBooksId(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_ID + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book ids. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding id: " + c.getString(c.getColumnIndex(MYBOOK_ID)));
				temp.add(Integer.parseInt(c.getString(c.getColumnIndex(MYBOOK_ID))));
				
			}
			Log.i("DatabaseHelper","my book ids obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybookids returning null");
			return null;
		
		}
	}
	
	public ArrayList<String> getMyBooksNames(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + MYBOOK_NAME + " FROM " + TABLE_CACHED_MYBOOKS;
		try{
			ArrayList<String> temp = new ArrayList<String>();
			Cursor c = database.rawQuery(sqlQ, null);
			//c.moveToFirst();
			Log.i("DatabaseHelper","forming list of my book names. count is " + c.getCount());
			while(c.moveToNext()){
				Log.i("DatabaseHelper", "Adding name: " + c.getString(c.getColumnIndex(MYBOOK_NAME)));
				temp.add(c.getString(c.getColumnIndex(MYBOOK_NAME)));
				
			}
			Log.i("DatabaseHelper","my book names obtained. returning.");
			return temp;
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","getmybooknames returning null");
			return null;
		
		}
	}
	
	
	
	public boolean addUserToDatabase(String login_id, String password){
		
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_USER_LOGIN;
			database.execSQL(sqlQ);
			ContentValues cv = new ContentValues();
			cv.put(LOGIN_ID, login_id);
			cv.put(PASSWORD, password);
			database.insert(TABLE_USER_LOGIN, null, cv);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean checkAnythingCached(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT COUNT(*) FROM " + TABLE_USER_LOGIN;
		try{
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			if(c.getInt(0) == 0){
				Log.i("DatabaseHelper", "table empty");
				return false;
			}
			else{
				Log.i("DatabaseHelper", "table contains data");
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	

	public String getFirstUser(){
		SQLiteDatabase database = this.getWritableDatabase();
		sqlQ = "SELECT " + LOGIN_ID + " FROM " + TABLE_USER_LOGIN;
		try{
			Cursor c = database.rawQuery(sqlQ, null);
			c.moveToFirst();
			Log.i("DatabaseHelper","returning " + c.getString(c.getColumnIndex(LOGIN_ID)));
			return c.getString(c.getColumnIndex(LOGIN_ID));
		}catch(Exception e){
			e.printStackTrace();
			Log.i("DatabaseHelper","returning ERROR");
			return "ERROR";
		
		}
	}
	
	public boolean clearUserLoginTable(){
		try{
			SQLiteDatabase database = this.getWritableDatabase();
			sqlQ = "DELETE FROM " + TABLE_USER_LOGIN;
			database.execSQL(sqlQ);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}

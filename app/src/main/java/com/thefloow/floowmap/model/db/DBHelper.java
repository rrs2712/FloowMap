package com.thefloow.floowmap.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rrs27 on 2018-01-11.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Log
    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();
    // Journey status
    public static String JOURNEY_BEGINS = "Started";
    public static String JOURNEY_ENDS = "Finished";
    // DB
    private static final String DB_NAME = "JourneyDB";
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase.CursorFactory DB_FACTORY = null;
    // Journey fields
    private final String TABLE_JOURNEY = "Journey";
    public static final String JOURNEY_ID = "_id";
    public static final String JOURNEY_NAME = "name";
    public static final String JOURNEY_STATUS = "status";
    public static final String JOURNEY_TIMESTAMP = "timestamp";
    // Location fields
    private final String TABLE_LOCATION = "Location";
    private final String LOCATION_ID = "_id";
    private final String LOCATION_LAT = "latitude";
    private final String LOCATION_LON = "longitude";
    private final String LOCATION_TIMESTAMP = "timestamp";
    // Location fields
    private final String TABLE_JOURNEY_LOCATION = "Journey_location";
    private final String JL_ID = "_id";
    private final String JL_JOURNEY_NAME = "name";
    private final String JL_LOCATION = "location_id";
    // Drop tables
    private final String DROP_TABLE_JOURNEY = "DROP TABLE IF EXISTS " + TABLE_JOURNEY + "; ";
    private final String DROP_TABLE_LOCATION = "DROP TABLE IF EXISTS " + TABLE_LOCATION + "; ";
    private final String DROP_TABLE_JOURNEY_LOCATION = "DROP TABLE IF EXISTS " + TABLE_JOURNEY_LOCATION + "; ";
    private final String DROP_ALL_TABLES =
            DROP_TABLE_JOURNEY +
            DROP_TABLE_LOCATION +
            DROP_TABLE_JOURNEY_LOCATION;
    // Create tables
    private final String CREATE_TABLE_JOURNEY = "CREATE TABLE IF NOT EXISTS " +
            TABLE_JOURNEY + "(" +
            JOURNEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            JOURNEY_NAME + " INTEGER, " +
            JOURNEY_STATUS + " TEXT(50), " +
            JOURNEY_TIMESTAMP + " INTEGER ); ";
    private final String CREATE_TABLE_LOCATION = "CREATE TABLE IF NOT EXISTS " +
            TABLE_LOCATION + "(" +
            LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LOCATION_LAT + " DOUBLE, " +
            LOCATION_LON + " DOUBLE, " +
            LOCATION_TIMESTAMP + " INTEGER ); ";
    private final String CREATE_TABLE_JL = "CREATE TABLE IF NOT EXISTS " +
            TABLE_JOURNEY_LOCATION + "(" +
            JL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            JL_JOURNEY_NAME + " DOUBLE, " +
            JL_LOCATION + " INTEGER, " +
            "FOREIGN KEY(" + JL_JOURNEY_NAME + ") REFERENCES " + TABLE_JOURNEY + "(" + JOURNEY_NAME + ")," +
            "FOREIGN KEY(" + JL_LOCATION + ") REFERENCES " + TABLE_LOCATION + "(" + LOCATION_ID + ") " +
            " ); ";
    private final String CREATE_ALL_TABLES =
            CREATE_TABLE_JOURNEY +
            CREATE_TABLE_LOCATION +
            CREATE_TABLE_JL;
    // Queries
    private final String SELECT_LAST_JOURNEY =
            "SELECT " +
                    JOURNEY_ID + ", " +
                    JOURNEY_NAME + ", " +
                    JOURNEY_STATUS + ", " +
                    JOURNEY_TIMESTAMP +
            " FROM " +
                    TABLE_JOURNEY +
            " ORDER BY " + JOURNEY_ID + " DESC" +
            " LIMIT 1;";


    public DBHelper(Context context) {
        super(context, DB_NAME, DB_FACTORY, DB_VERSION);
        Log.d(TAG,"DBHelper");
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_ALL_TABLES);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade, from version " + oldVersion + " to " + newVersion);
        db.execSQL(DROP_ALL_TABLES);
        onCreate(db);
    }

    public boolean saveJourney(int name, long timeStamp, String status){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(JOURNEY_NAME,name);
        cv.put(JOURNEY_TIMESTAMP,timeStamp);
        cv.put(JOURNEY_STATUS,status);

        long result = db.insert(TABLE_JOURNEY,null, cv);
        return (result == -1) ? false : true;
    }

    public Cursor getLastJourney(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(SELECT_LAST_JOURNEY, null);
    }
}

package iut.dam.powerhome.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table User
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_FIRSTNAME = "firstName";
    private static final String COLUMN_USER_LASTNAME = "lastName";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static  final String COLUMN_USER_TELEPHONE = "telephone";
    private static final String COLUMN_USER_HABITAT_ID = "habitat_id";

    // Table Habitat
    private static final String TABLE_HABITAT = "Habitat";
    private static final String COLUMN_HABITAT_ID = "id";
    private static final String COLUMN_HABITAT_RESIDENTNAME = "residentName";
    private static final String COLUMN_HABITAT_FLOOR = "floor";
    private static final String COLUMN_HABITAT_AREA = "area";

    // Table Appliance
    private static final String TABLE_APPLIANCE = "Appliance";
    private static final String COLUMN_APPLIANCE_ID = "id";
    private static final String COLUMN_APPLIANCE_NAME = "name";
    private static final String COLUMN_APPLIANCE_REFERENCE = "reference";
    private static final String COLUMN_APPLIANCE_WATTAGE = "wattage";
    private static final String COLUMN_APPLIANCE_HABITAT_ID = "habitat_id";

    // Table TimeSlot
    private static final String TABLE_TIMESLOT = "TimeSlot";
    private static final String COLUMN_TIMESLOT_ID = "id";
    private static final String COLUMN_TIMESLOT_BEGIN = "begin";
    private static final String COLUMN_TIMESLOT_END = "end";
    private static final String COLUMN_TIMESLOT_MAXWATTAGE = "maxWattage";

    // Table Booking
    private static final String TABLE_BOOKING = "Booking";
    private static final String COLUMN_BOOKING_ID = "id";
    private static final String COLUMN_BOOKING_ORDER = "order";
    private static final String COLUMN_BOOKING_BOOKEDAT = "bookedAt";
    private static final String COLUMN_BOOKING_APPLIANCE_ID = "appliance_id";
    private static final String COLUMN_BOOKING_TIMESLOT_ID = "timeSlot_id";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Créer les tables
        db.execSQL("CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_FIRSTNAME + " TEXT,"
                + COLUMN_USER_LASTNAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_TELEPHONE + " TEXT,"
                + COLUMN_USER_HABITAT_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_HABITAT_ID + ") REFERENCES " + TABLE_HABITAT + "(" + COLUMN_HABITAT_ID + ")"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_HABITAT + "("
                + COLUMN_HABITAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HABITAT_RESIDENTNAME + " TEXT,"
                + COLUMN_HABITAT_FLOOR + " INTEGER,"
                + COLUMN_HABITAT_AREA + " REAL"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_APPLIANCE + "("
                + COLUMN_APPLIANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_APPLIANCE_NAME + " TEXT,"
                + COLUMN_APPLIANCE_REFERENCE + " TEXT,"
                + COLUMN_APPLIANCE_WATTAGE + " INTEGER,"
                + COLUMN_APPLIANCE_HABITAT_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_APPLIANCE_HABITAT_ID + ") REFERENCES " + TABLE_HABITAT + "(" + COLUMN_HABITAT_ID + ")"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_TIMESLOT + "("
                + COLUMN_TIMESLOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TIMESLOT_BEGIN + " DATETIME,"
                + COLUMN_TIMESLOT_END + " DATETIME,"
                + COLUMN_TIMESLOT_MAXWATTAGE + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_BOOKING + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOKING_ORDER + " INTEGER,"
                + COLUMN_BOOKING_BOOKEDAT + " DATETIME,"
                + COLUMN_BOOKING_APPLIANCE_ID + " INTEGER,"
                + COLUMN_BOOKING_TIMESLOT_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_BOOKING_APPLIANCE_ID + ") REFERENCES " + TABLE_APPLIANCE + "(" + COLUMN_APPLIANCE_ID + "),"
                + "FOREIGN KEY(" + COLUMN_BOOKING_TIMESLOT_ID + ") REFERENCES " + TABLE_TIMESLOT + "(" + COLUMN_TIMESLOT_ID + ")"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprimer les tables si elles existent
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLIANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESLOT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }

    public long addUser(String firstName, String lastName, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Ajouter les valeurs dans ContentValues
        values.put(COLUMN_USER_FIRSTNAME, firstName);
        values.put(COLUMN_USER_LASTNAME, lastName);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_TELEPHONE, phone); // Assurez-vous que cette colonne existe dans votre table User

        // Insérer l'utilisateur dans la table User
        long id = db.insert(TABLE_USER, null, values);
        db.close();

        return id; // Retourne l'ID de l'utilisateur inséré ou -1 en cas d'erreur
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + " = ?", new String[]{email},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }
}

package com.example.anand_roadwayss;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBAdapter {

    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB5";
    static final int DATABASE_VERSION = 9;

    private static final String KEY_ROWID = "_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_VOUCHER_NO = "VouNo";
    private static final String KEY_VEHICLE_NO = "VehNo";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_NAME = "Name";
    private static final String KEY_PHNO = "PhNo";


    // 1. Begin Registration Table details

    private static final String KEY_REG_MAIL = "Email";
    private static final String KEY_PIN = "pin";
    private static final String T_REG = "Registration";
    private static final String KEY_ISREG = "isReg";
    private static final String KEY_PRODUCTKEY = "ProductKey";
    private static final String KEY_USERTYPE = "UserType";
    private static final String KEY_CLIENTNAME = "ClientName";


    static final String DATABASE_REG = "create table Registration (_id integer primary key autoincrement, "
            + "Name text , Email text ,PhNo text ,pin text,isReg integer, ProductKey text , UserType text , ClientName text);";


    // 1. End Registration Table details

    // 2. Begin Fuel Details Table

    private static final String KEY_MDATE = "Date";
    private static final String KEY_MVEHICLE = "Vehicle";
    private static final String KEY_MDRIVER = "Driver";
    private static final String KEY_SPEEDOMETER = "SpeedMeter";
    private static final String KEY_FUEL = "FuelVolume";
    private static final String KEY_FUELROWID = "FuelRowId";
    private static final String T_FUEL_DETAILS = "FuelDetails";

    static final String DATABASE_FUEL_DETAILS = "create table FuelDetails (_id integer primary key autoincrement, "
            + "Date text not null , Vehicle text not null ,Driver text not null , SpeedMeter text , FuelVolume text,FuelRowId text );";

    // 2. End Fuel Details Table

    // 3. Begin Advance Details Table

    private static final String KEY_ADVANCE_TYPE = "AdvanceType";
    private static final String T_ADVANCE_DETAILS = "AdvanceDetails";
    private static final String KEY_RECEIPT="voucherImage";

    private static final String DATABASE_ADVANCE_DETAILS = "create table AdvanceDetails (_id integer primary key autoincrement, "
            + "date text,VehNo text,Name text,AdvanceType text,VouNo text , amount integer, voucherImage blob );";

    // 3. End Advance Details Table

    // 4. Begin Payment Details Table

    private static final String T_PAYMENT_DETAILS = "PaymentDetails";
    private static final String KEY_PAYMENT_TYPE = "PaymentType";
    private static final String KEY_COMMISSION = "Commission";
    static final String KEY_DEDUCTION = "Deduction";
    private static final String KEY_MILEAGEDEDUCTION = "MileageDeduction";

    private static final String DATABASE_PAYMENT_DETAILS = "create table PaymentDetails (_id integer primary key autoincrement, "
            + "date text not null,PaymentType text not null,Name text not null,VouNo text not null,Commission text not null,Deduction text,MileageDeduction text, amount text not null, voucherImage blob );";

    // 4. End Payment Details Table

    // 5. Begin Vehicle Details Table

    private static final String KEY_VEH_TYPE = "vehType";
    private static final String KEY_VEH_MILEAGE = "StdMileage";
    private static final String KEY_IMEI = "Imei";
    private static final String KEY_VEH_PH_NO = "VehPhNo";

    private static final String T_VEHICE_DETAILS = "VehicleDetails";
    private static final String DATABASE_VEHICLE_DETAILS = "create table VehicleDetails (_id integer primary key autoincrement, "
            + "VehNo text , vehType text ,StdMileage text , Imei text,VehPhNo text );";

    // 5. End Vehicle Details Table

    // 6. Begin Employee Details Table

    private static final String KEY_MANAGETYPE = "ManageType";
    private static final String KEY_ADDR = "Address";
    private static final String KEY_LIC_NO = "LicenseNo";
    private static final String KEY_PHOTO = "BlobPhoto";
    private static final String KEY_EMPLOYEE_ID = "EmpId";
    private static final String KEY_SALARY = "Salary";

    private static final String T_EMPLOYEE_DETAILS = "EmployeeDetails";

    private static final String DATABASE_EMPLOYEE_DETAILS = "create table EmployeeDetails (_id integer primary key autoincrement, "
            + "Name text ,ManageType text ,PhNo text,Address text,LicenseNo text,BlobPhoto blob,EmpId text ,Salary int ,Commission int );";

    // 6. End Employee Details Table

    // 7. Begin Location Table

    private static final String KEY_LOC_NAME = "Lname";
    private static final String KEY_LONGITUDE = "long";
    private static final String KEY_LATITUDE = "lati";
    private static final String KEY_DISTANCE = "kms";
    private static final String KEY_LOCATIONTYPE = "ltype";
    private static final String T_LOCATION = "Location";

    private static final String DATABASE_LOCATION = "create table Location (_id integer primary key autoincrement, "
            + "Lname text not null , long text not null ,lati text not null,ltype text);";

    // 7. End Location Table



    // 8. Begin InboxDetails Table

    private static final String KEY_SUBJECT = "Subject";
    private static final String KEY_MESSAGE = "Message";
    private static final String KEY_FLAG = "Flag";
    private static final String T_INBOX = "InboxDetails";

    private static final String DATABASE_INBOX_DETAILS = "create table InboxDetails (_id integer primary key autoincrement, "
            + "date text , Subject text, Message text, Flag text );";

    // 8. End InboxDetails Table

    // 9. Begin signature Table
    private static final String KEY_SIGN="signature";
    private static final String T_SIGN = "RegSign";

    private static final String DATABASE_SIGNATURE = "create table RegSign (_id integer primary key autoincrement , signature blob );";
    //9. Begin signature Table



    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_REG);
                db.execSQL(DATABASE_FUEL_DETAILS);
                db.execSQL(DATABASE_ADVANCE_DETAILS);
                db.execSQL(DATABASE_PAYMENT_DETAILS);
                db.execSQL(DATABASE_VEHICLE_DETAILS);
                db.execSQL(DATABASE_EMPLOYEE_DETAILS);
                db.execSQL(DATABASE_LOCATION);
                db.execSQL(DATABASE_INBOX_DETAILS);
                db.execSQL(DATABASE_SIGNATURE);


            } catch (SQLException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to"
                    + newVersion + ", which will destroy all old data");

        }
    }

    // ---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---closes the database---
    public void close() {
        DBHelper.close();
    }

    //1. To Delete Table
    public void deleteRow(String table) {

        db.execSQL("delete from " + table);
    }



    //3. To Delete Employee Details From Employee Table By EmpId
    public boolean deleteEmployee(String table, String EmpId) {
        String WhereClouse = "EmpId = ?";
        String[] whereArgs = new String[]{EmpId};
        return db.delete(table, WhereClouse, whereArgs) > 0;

    }

    //4. To Delete Fuel From Fuel Table By RowId(_id)
    public boolean deleteFuel(String id) {
        String WhereClouse = "_id = ?";
        String[] whereArgs = new String[]{id};
        return db.delete(DBAdapter.T_FUEL_DETAILS, WhereClouse, whereArgs) > 0;
    }

    //5. To Delete Particular Row From Table By Row String
    public boolean deletePerticularrow(String table, String row, String id) {
        return db.delete(table, row + "=" + id, null) > 0;

    }

    //6. To Delete Vehicle From Vehicle Table By VehNo
    public boolean deleteVehicle(String table, String EmpId) {
        String WhereClouse = "VehNo = ?";
        String[] whereArgs = new String[]{EmpId};
        return db.delete(table, WhereClouse, whereArgs) > 0;

    }

    public boolean deletePayment(String table, String voucher) {
        String WhereClouse = "VouNo = ?";
        String[] whereArgs = new String[]{voucher};
        return db.delete(table, WhereClouse, whereArgs) > 0;

    }

    //8. To Delete Lcation From Location Table By RowId(_id)
    public boolean deleteDest(String table, String id) {
        return db.delete(table, KEY_ROWID + "=" + id, null) > 0;

    }

    //9. To Check VehNo In Vehicle Table By Vehicle no
    public String checkvehTforDataExist(String vehPin) {
        Cursor cursor = db.query(getVehicleDetails(), null, " VehNo=?",
                new String[]{vehPin}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String Vno = cursor.getString(cursor.getColumnIndex("VehNo"));
        cursor.close();
        return Vno;
    }

    //10. To Check Driver Name In Employee Table By Driver Name
    public String checkDrvierTableforDataExist(String driverName) {
        Cursor cursor = db.query(getEmployeeDetails(), null,
                " Name=? AND ManageType=?",
                new String[]{driverName, "Driver"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String DrvId = cursor.getString(cursor.getColumnIndex("EmpId"));
        cursor.close();
        return DrvId;
    }

    //11. To Check Fuel in Fuel Table By RowId
    public String checkFuelTableforDataExist(String rowId) {
        Cursor cursor = db.query(getFuelDetails(), null,
                " FuelRowId=?",
                new String[]{rowId}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String FuelId = cursor.getString(cursor.getColumnIndex("_id"));
        cursor.close();
        return FuelId;
    }

    //12. To Check Advance In advance Table By Voucher no
    public String checkAdvanceTableforDataExist(String voucher) {
        Cursor cursor = db.query(getAdvanceDetails(), null,
                "VouNo=?",
                new String[]{voucher}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String advanceVNo = cursor.getString(cursor.getColumnIndex("VouNo"));
        cursor.close();
        return advanceVNo;
    }

    //13. To Check Payment In Payment Table By Voucher no
    public String checkPaymentTableforDataExist(String voucher) {
        Cursor cursor = db.query(getPaymentDetails(), null,
                "VouNo=?",
                new String[]{voucher}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String paymentVNo = cursor.getString(cursor.getColumnIndex("VouNo"));
        cursor.close();
        return paymentVNo;
    }


    //15. To Check Location From Location Table By Location name
    public String checkLocationTableforDataExist(String locationName) {
        Cursor cursor = db.query(getLocation(), null,
                "Lname=?",
                new String[]{locationName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String location = cursor.getString(cursor.getColumnIndex("_id"));
        cursor.close();
        return location;
    }

    //16. To Check Cleaner from Employee Table By Name
    public String checkCleanerTableforDataExist(String cleanerName) {
        Cursor cursor = db.query(getEmployeeDetails(), null,
                " Name=? AND ManageType=?", new String[]{cleanerName,
                        "Cleaner"}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String ClnrId = cursor.getString(cursor.getColumnIndex("EmpId"));
        cursor.close();
        return ClnrId;
    }

    //17. To Check Location From Location Table By Location name
    public String checkDestTableforDataExist(String LocName) {
        Cursor cursor = db.query(T_LOCATION, null, " Lname=?",
                new String[]{LocName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        // cursor.moveToFirst();
        // String DrvId = cursor.getString(cursor.getColumnIndex("EmpId"));
        cursor.close();
        return "EXIST";
    }

    //18. To Check Location From location Table By Source Type
    public String checkLocationTableforDataExist() {
        String type = "SOURCE";
        Cursor cursor = db.query(T_LOCATION, null, " ltype=?",
                new String[]{type}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        // cursor.moveToFirst();
        // String DrvId = cursor.getString(cursor.getColumnIndex("EmpId"));
        cursor.close();
        return "EXIST";
    }


    //20. To Retrieve Phone Number From Registration Table By Phone Number
    public String retrieve(String PhNo) {
        Cursor cursor = db.query(T_REG, null, " PhNo=?", new String[]{PhNo},
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String PhNo1 = cursor.getString(cursor.getColumnIndex("PhNo"));
        cursor.close();
        return PhNo1;
    }

    //21. To Retrieve Pin From Registration Table By Phone Number
    public String retrievePin(String Phone) {
        Cursor cursor = db.query(T_REG, null, " PhNo=?",
                new String[]{Phone}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String pin = cursor.getString(cursor.getColumnIndex("pin"));
        cursor.close();
        return pin;
    }

    //22. To Retrieve Profile Data From Registration Table
    public Cursor retrieveProfileData() {
        Cursor cursor = db.query(T_REG, null, null, null,
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();

        }
        return cursor;
    }

    //23. To Retrieve Inbox Data From Inbox Table
    public Cursor retrieveInboxData() {
        Cursor cursor = db.query(T_INBOX, null, null, null,
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();

        }
        return cursor;
    }

    //24. Checking Registratio Table For GCM Registration by Phone No
    public int isRegistered(String phone) {
        int isReg = 0;
        Cursor y = db.rawQuery("SELECT " + KEY_ISREG + " FROM " + T_REG
                + " ORDER BY " + KEY_ROWID + " LIMIT 1", null);
        if (y.moveToFirst()) {
            do {
                int i = y.getInt(0);
                isReg = i;

            } while (y.moveToNext());

        }
        return isReg;
    }

//    //25. Checking Trip Table By Vehicle And Driver Name
//    public Cursor insertTrip(String Vehicle, String Driver) {
//        String[] whereargs = new String[]{Vehicle, Driver};
//        String Query = "SELECT * FROM SourceDestDetails WHERE VehNo = ? OR Driver = ?";
//        return db.rawQuery(Query, whereargs);
//
//    }

    //26. Insert a Table values into the Table
    public long insertContact(String TABLE, ContentValues contentValues) {

        return db.insert(TABLE, null, contentValues);
    }

    //27. Insert Upto 100 rows
    public long insertContactWithDelete(String TABLE, ContentValues contentValues)
    {
        long id = db.insert(TABLE, null, contentValues);
        Cursor c = db.rawQuery("select (max(_id)-100)  from " + TABLE, null);
        String l = String.valueOf(c.moveToFirst() ? c.getInt(0) : 0);
        String WhereClouse = "_id <= ?";
        String[] whereArgs = new String[]{l};
        boolean i = db.delete(TABLE, WhereClouse, whereArgs) > 0;
        if (i) {

        }

        return id;
    }
    public long updateVehicle(ContentValues cv)
    {
        int  a=  db.update(T_ADVANCE_DETAILS, cv, "VehNo=?", new String[]{"KA 001"});
        //int b= db.update(T_ADVANCE_DETAILS,cv,"VehNo=?" ,new String[]{"KA 001"});
        //int c=db.update(T_ADVANCE_DETAILS,cv,"VehNo=?" ,new String[]{"KA 001"});
        return a;
    }



    //29. Update a -----------------------
    public long updateContact(String TABLE, ContentValues contentValues, String id) {

        return db.update(TABLE, contentValues, "EmpId " + "=" + id, null);
    }

    //30. Update a------------------------
    public long updateContact(String table, ContentValues values,
                              String employeeName, String employeeType) {

        return db.update(table, values, "Name =? and ManageType=?",
                new String[]{employeeName, employeeType});
    }

    //31. Update Cleaner Details In Employee Table
    public long updateCleaner(String table, ContentValues values,
                              String employeeName) {

        return db.update(table, values, "ClnrName =?",
                new String[]{employeeName});
    }

    //32. Updating Vehicle in Vehicle Table
    public long updateVehicleContact(String table, ContentValues values,
                                     String vehicleUpdate) {

        return db.update(table, values, "VehNo =?",
                new String[]{vehicleUpdate});
    }

    //33. Updating Registration For GCM By Phone No
    public void updateRegId(ContentValues values, String pno) {
        db.update(T_REG, values, "PhNo =?", new String[]{pno});

    }

    //34. Updating Location Details In Location table By LName
    public long updateDest(String tDestination, ContentValues cv,
                           String DestName) {
        return db.update(tDestination, cv, "Lname =?",
                new String[]{DestName});
    }

    //35. Updating Profile
    public long updateProfile(String table, ContentValues cv) {
        return db.update(table, cv, null, null);
    }

    //36. Getting all Employee By EmpType
    public Cursor getAllEmp(String table, String type) {

        String buildSQL = "SELECT * FROM " + table + " WHERE " + KEY_MANAGETYPE
                + " = " + "'" + type + "'" + "ORDER BY Name ASC";

        return db.rawQuery(buildSQL, null);
    }

    //37. Getting All Fuel Details From Fuel table
    public Cursor getAllFuelDetails() {

        return db.query(T_FUEL_DETAILS, new String[]{KEY_ROWID, KEY_MDATE,
                        KEY_MVEHICLE, KEY_MDRIVER, KEY_SPEEDOMETER, KEY_FUEL}, null,
                null, null, null, KEY_MDATE + " DESC", "50");
    }

    //38.Getting all Data From Table
    public Cursor getAllData(String table) {

        String buildSQL = "SELECT * FROM " + table;

        return db.rawQuery(buildSQL, null);
    }

    //39. Getting All Vehicle Details
    public Cursor getAllVehicleData(String table) {

        String buildSQL = "SELECT * FROM " + table + " ORDER BY "
                + getKeyVehicleNo() + " ASC";

        return db.rawQuery(buildSQL, null);
    }

    //40. Getting All Employee Details for manageType
    public Cursor getData(String table, String column) {

        String[] whereargs = new String[]{column};
        String buildSQL = "SELECT * FROM " + table + " WHERE ManageType = ?"
                + "ORDER BY Name ASC";
        return db.rawQuery(buildSQL, whereargs);
    }

    //41. Getting Values*********************
    public Cursor getParticularData(String table, String columnInput,
                                    String value) {

        String buildSQL = "select * from" + table + " where " + columnInput
                + "=" + "'" + value + "'";

        return db.rawQuery(buildSQL, null);
    }



    //44. Getting All *****************
    public List<String> getAllDetails(String table) {
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + table;
//		db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return labels;
    }

    //45. Getting All*********************
    public List<String> getAllLabels(String table) {
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + table + " ORDER BY "
                + getKeyVehicleNo() + " ASC";
//		db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return labels;
    }

    //46. **********************************
    public List<String> getAllLabels(String table, String EmployeeType) {
        List<String> labels = new ArrayList<String>();
        // String selectQuery = "SELECT  * FROM " + table;
        db = DBHelper.getReadableDatabase();
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(table, null, " ManageType=? ",
                new String[]{EmployeeType}, null, null, "Name ASC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return labels;
    }

    //47. Getting Different List Of Location For Type
    public List<String> getLocationForSpinner(String LocType) {
        List<String> labels = new ArrayList<String>();
        String selectQuery = "select * from " + T_LOCATION + " WHERE "
                + DBAdapter.KEY_LOCATIONTYPE + "='" + LocType + "'";
        db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return labels;
    }

    //48. Getting All Advance Details For Different Types
    public Cursor getAllContactsVehicleAdvance(String advanceType)
    {
        Cursor c = db.rawQuery("select * from " + T_ADVANCE_DETAILS + " WHERE "
                + DBAdapter.KEY_ADVANCE_TYPE + "='" + advanceType + "'"
                + " ORDER BY " + getKeyDate() + " ASC" + " LIMIT 100", null);
        return c;
    }

    //49. Getting All***********************
    public Cursor getAllDataInOrder(String tableName, String columnName,
                                    String advanceType) {
        Cursor c = db.rawQuery("select * from " + tableName + " WHERE "
                + DBAdapter.KEY_ADVANCE_TYPE + "='" + advanceType + "'"
                + " ORDER BY " + columnName + " ASC" + " LIMIT 100", null);
        return c;
    }

    //50. Getting All***********************
    public Cursor getAllContactsDriverAdvance(String advanceType) {
        Cursor c = db.rawQuery("select * from " + T_ADVANCE_DETAILS + " WHERE "
                        + DBAdapter.KEY_ADVANCE_TYPE + "='" + advanceType + "'"
                        + " ORDER BY " + getKeyVoucherNo() + " ASC" + " LIMIT 100",
                null);
        return c;
    }

    //51. Getting All ***********************
    public Cursor getAllContactsCleanerAdvance(String advanceType) {
        Cursor c = db.rawQuery("select * from " + T_ADVANCE_DETAILS + " WHERE "
                        + DBAdapter.KEY_ADVANCE_TYPE + "='" + advanceType + "'"
                        + " ORDER BY " + getKeyVoucherNo() + " ASC" + " LIMIT 100",
                null);
        return c;
    }

    //52. Getting All Driver Payments *********************
    public Cursor getAllDriverPayments() {
        return db.query(getPaymentDetails(),
                new String[]{KEY_ROWID, getKeyDate(), getKeyName(),
                        getKeyVoucherNo(), getKeyAmount()}, "PaymentType=?",
                new String[]{"Driver"}, null, null, getKeyDate() + " DESC",
                "100");
    }

    //53. Getting All Cleaner Payments
    public Cursor getAllCleanerPayments() {
        return db.query(getPaymentDetails(),
                new String[]{KEY_ROWID, getKeyDate(), getKeyName(),
                        getKeyVoucherNo(), getKeyAmount()}, "PaymentType=?",
                new String[]{"Cleaner"}, null, null, getKeyDate() + " DESC",
                "100");
    }

    //54. Getting Driver Details By Name
    public Cursor getDriverEntry(String name) {
        return db.query(getEmployeeDetails(), null, " Name=?",
                new String[]{name}, null, null, null);
    }

    //55. Getting Password
    public String getSinlgeEntry(String Pin) {
        Cursor cursor = db.query(T_REG, null, " pin=?", new String[]{Pin},
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("pin"));
        cursor.close();
        return password;
    }


    //57. Getting Employee Details For _id
    public Cursor getDriverEntryID(String EmpId) {
        return db.query(T_EMPLOYEE_DETAILS, null, " EmpId=?",
                new String[]{EmpId}, null, null, null);
    }

    //57. Getting Employee Details For name
    public Cursor getDriverEntryName(String EmpName) {
        return db.query(T_EMPLOYEE_DETAILS, null, " Name=?",
                new String[]{EmpName}, null, null, null);
    }
    //58. Getting *****************
    public Cursor getCleanerEntry(String name1) {
        return db.query(getEmployeeDetails(), null, " Name=?",
                new String[]{name1}, null, null, null);
    }

    //59. Getting Emloyee Details From Employee By EMP Id
    public String getEmployeeName(String empId, String table, String columnName) {
        Cursor cursor = db.query(table, null, " EmpId=?",
                new String[]{empId}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        return name;
    }

    //60. Getting *****************************
    public String getEmployeeId(String empName, String table, String columnName) {
        Cursor cursor = db.query(table, null, " Name=? COLLATE NOCASE",
                new String[]{empName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(columnName));
        cursor.close();
        return id;
    }

    //61. Getting Vehicle details For VehNo
    public Cursor getVehicleEntry(String veh) {
        return db.query(getVehicleDetails(), null, " VehNo=?",
                new String[]{veh}, null, null, null);
    }

    //62. Getting Phone From Registration Table
    public String getphone() {
        String RegPhNo = null;
        Cursor y = db.rawQuery("SELECT " + KEY_PHNO + " FROM " + T_REG
                + " ORDER BY " + KEY_ROWID + " LIMIT 1", null);
        if (y.moveToFirst()) {
            do {
                String phone = y.getString(0); // Here you can get data from
                RegPhNo = phone;

            } while (y.moveToNext());
        }
        return RegPhNo;
    }

    //63. Getting All Location
    public Cursor getAllDestination() {
        return db.query(T_LOCATION, new String[]{KEY_ROWID, KEY_LOC_NAME,
                        KEY_LOCATIONTYPE}, null, null, null,
                null, null, null);
    }

    //64. Getting Location Details By _id
    public Cursor getDestinationDetails(String dest_Id) {
        String whereclouse = "_id=?";
        String[] whereArgs = new String[]{dest_Id};
        Cursor destDetails = db.query(T_LOCATION, new String[]{KEY_ROWID,
                        KEY_LONGITUDE, KEY_LATITUDE, KEY_DISTANCE, KEY_AMOUNT},
                whereclouse, whereArgs, null, null, null);
        return destDetails;
    }

    //65. Getting Location Details By _id
    public Cursor getLatLongDest(long id) {
        String rowid = String.valueOf(id);
        String whereclouse = "_id=?";
        Cursor cursor = db.query(T_LOCATION, new String[]{KEY_ROWID,
                        KEY_LONGITUDE, KEY_LATITUDE, KEY_LOC_NAME,KEY_LOCATIONTYPE}, whereclouse,
                new String[]{rowid}, null, null, null);
        return cursor;
    }

    //66. Getting Cleaner Commision for Name
    public int getCleanerCommission(String empName, String table) {
        int a = 0;
        if (empName.equals("NO CLEANER")) {
            String type = "Cleaner";
            Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE "
                    + DBAdapter.KEY_MANAGETYPE + "='" + type + "'"
                    + " ORDER BY " + KEY_ROWID + " LIMIT 1", null);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    a = c.getInt(9);
                }
            } else {
                a = 0;
            }
        } else {
            Cursor c = db.query(table, null, "Name=?",
                    new String[]{empName}, null, null, null);
            if (c.moveToFirst()) {
                a = c.getInt(9);
            }
        }
        return a;
    }



    //68. Getting Payment Voucher For _id
    public Cursor getVoucherPayment(String id) {
        String whereclouse = "_id=?";
        String[] whereArgs = new String[]{id};
        Cursor PaymentDetails = db.query(T_PAYMENT_DETAILS,
                new String[]{KEY_VOUCHER_NO}, whereclouse, whereArgs, null,
                null, null);
        return PaymentDetails;
    }

    //69. Getting advance Voucher For _id
    public Cursor getVoucherAdvance(String id) {
        String whereclouse = "_id=?";
        String[] whereArgs = new String[]{id};
        Cursor AdvanceDetails = db.query(T_ADVANCE_DETAILS,
                new String[]{KEY_VOUCHER_NO}, whereclouse, whereArgs, null,
                null, null);
        return AdvanceDetails;
    }

    public Cursor getVehNumAdvance(String id)
    {
        return db.query(T_ADVANCE_DETAILS,null,"_id=?",new String[]{id},null,null,null);
    }

    public Cursor getDriverAdvance(String id)
    {
        return db.query(T_ADVANCE_DETAILS,null,"_id=?",new String[]{id},null,null,null);
    }
    //70. Getting Fuel By _id
    public Cursor getOneFuelDetails(String id) {
        return db.query(T_FUEL_DETAILS, new String[]{KEY_FUELROWID},
                "_id=?", new String[]{id}, null, null, null);
    }
    public Cursor getOneVehicleFuelDetails(String id) {
        return db.query(T_FUEL_DETAILS, new String[]{KEY_MVEHICLE},
                "_id=?", new String[]{id}, null, null, null);
    }

    //71. Getting Inbox By _id
    public Cursor getInboxDetails(String id) {
        return db.query(getInboxDetails(), null, "_id=?",
                new String[]{id}, null, null, null);
    }


    //73. Getting All Data For Table By RowId
    public Cursor getAll(String table, String id) {
        Cursor Details = db.query(table,
                new String[]{id}, null, null, null, null, null);
        return Details;
    }

    //74. Getting Employee details By EmpId
    public Cursor getEmployeeTableDetails(String table, String id) {
        return db.query(table, null, "EmpId=?",
                new String[]{id}, null, null, null);
    }

    //75. Getting Location By Name
    public Cursor getLocationEntry(String loc) {
        return db.query(getLocation(), null, "Lname=?",
                new String[]{loc}, null, null, null);
    }

    //76. Getting Fuel From _id
    public Cursor getFuelEntry(String id) {
        return db.query(getFuelDetails(), null, "FuelRowId=?",
                new String[]{id}, null, null, null);
    }

    //77. Getting Advance Entry
    public Cursor getAdvanceEntry(String table, String voucher) {
        return db.query(table, null, "VouNo=?",
                new String[]{voucher}, null, null, null);
    }

    //78 Delete Advance ,Payment
    public boolean deleteLocaly(String table, String id) {
        String WhereClouse = "_id = ?";
        String[] whereArgs = new String[]{id};
        return db.delete(table, WhereClouse, whereArgs) > 0;

    }
    // 79 deleteLocation
    public boolean deleteLocation(String table, String id) {
        return db.delete(table, KEY_LOC_NAME + "=" + id, null) > 0;

    }

    //80. To Check VehNo In Vehicle Table By Vehicle no
    //public String checkExpenseTableforDataExist(String voucher) {
    //   Cursor cursor = db.query(getExpensedetails(), null, " VouNo=?",
    //         new String[]{voucher}, null, null, null);
    // if (cursor.getCount() < 1) // UserName Not Exist
    //  {
    //      cursor.close();
    //      return "NOT EXIST";
    //  }
    //  cursor.moveToFirst();
    // String Vno = cursor.getString(cursor.getColumnIndex("VouNo"));
    // cursor.close();
    // return Vno;
    // }


    //81. Updating Expense Details In Expense table By Voucher Num
    // public long updateExpense(String table, ContentValues cv,
    //                       String voucher) {
    //    return db.update(table, cv, "VouNo =?", new String[]{voucher});
    // }

    //82. Getting Expense Details By VoucherNo
    // public Cursor getExpenseEntry(String voucherNo) {
    //     return db.query(getExpensedetails(), null, " VouNo=?",
    //            new String[]{voucherNo}, null, null, null);
    // }

//    //83. To Retrieve Inbox Data From Inbox Table
//    public Cursor getParticularExpenseData(String startDate,String endDate) {
//        Cursor cursor = db.rawQuery("select _id, VehNo, Name, date, VouNo, Particular, amount, BlobPhoto from " + T_EXPENSEDETAILS + " where Date BETWEEN '" + startDate + "' AND '" + endDate + "' ORDER BY date ASC", null);
//
//        if (cursor.getCount() < 1) // UserName Not Exist
//        {
//            cursor.close();
//
//        }
//        return cursor;
//    }

    //84. Getting expense Voucher For _id
    public Cursor getVoucherExpense(String table,String id) {
        String whereclouse = "_id=?";
        String[] whereArgs = new String[]{id};
        Cursor AdvanceDetails = db.query(table,
                new String[]{KEY_VOUCHER_NO}, whereclouse, whereArgs, null,
                null, null);
        return AdvanceDetails;
    }


    //86 Getting All EmployeeDetais from table

    public Cursor retrieveAllDataFromTable(String table) {
        Cursor cursor = db.query(table, null, null, null,
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();

        }
        return cursor;
    }
    //87 inserting signature to table

    public void addSignature( byte[] image)
    {
        ContentValues cv = new  ContentValues();
        cv.put(KEY_SIGN,   image);
        db.insert( T_SIGN, null, cv );
    }

    //getting sign for comparison advance n payment module
    public Cursor getSignature()
    {
        Cursor c;
        String sign = "SELECT signature FROM "+T_SIGN;
        c = db.rawQuery(sign, null);
        c.moveToFirst();
        return c;
    }

    //9. To Check VehNo In Vehicle Table By Vehicle no
    public String checkTforSignExist() {
        Cursor cursor = db.query(T_SIGN, null, null,
                null, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
         String Vno = "EXIST";
        cursor.close();

        return Vno;
    }

    //88 DB Version
    public static int getDBVersion(){
        return DATABASE_VERSION;
    }




    public boolean deleteAllRowsOfTable(String table){
        return db.delete(table,null,null)>0;
    }

    //90. getting count of unread message
    public int unreadMessageCount()
    {
        Cursor c=db.rawQuery(" select Flag from InboxDetails where Flag =0 ", null);
        int count=c.getCount();
        return count;
    }
    //91 reducing count of unread message after reading message
    public void afterReadMessage(long id)
    {
        ContentValues cv=new ContentValues();
        cv.put("Flag",1);
        db.update("InboxDetails", cv, "_id=" + id, null);
    }
    //92 deleting inbox messages
    public void deleteInboxItem(long id)
    {
        db.delete("InboxDetails","_id=" + id,null);
    }

//93
    public void deleteAllInboxItem()
    {
        db.execSQL("delete from "+ "InboxDetails");
    }


    //******* START THE GETTER METHODS FROM HERE********
    public static String getVoucherImage()
    {
        return KEY_RECEIPT;
    }
    public static String getEmployeeDetails() {
        return T_EMPLOYEE_DETAILS;
    }

    public static String getKeyName() {
        return KEY_NAME;
    }

    public static String getKeyMdate() {
        return KEY_MDATE;
    }

    public static String getKeyMvehicle() {
        return KEY_MVEHICLE;
    }

    public static String getKeyMdriver() {
        return KEY_MDRIVER;
    }

    public static String getKeySpeedometer() {
        return KEY_SPEEDOMETER;
    }

    public static String getKeyFuel() {
        return KEY_FUEL;
    }

    public static String getFuelDetails() {
        return T_FUEL_DETAILS;
    }

    public static String getKeyDate() {
        return KEY_DATE;
    }


    public static String getKeyVoucherNo() {
        return KEY_VOUCHER_NO;
    }

    public static String getKeyAmount() {
        return KEY_AMOUNT;
    }

    public static String getKeyPaymentType() {
        return KEY_PAYMENT_TYPE;
    }

    public static String getKeyCommission() {
        return KEY_COMMISSION;
    }

    public static String getPaymentDetails() {
        return T_PAYMENT_DETAILS;
    }

    public static String getKeyMileagededuction() {
        return KEY_MILEAGEDEDUCTION;
    }

    public static String getKeyPhoto() {
        return KEY_PHOTO;
    }

    public static String getKeyVehType() {
        return KEY_VEH_TYPE;
    }

    public static String getKeyVehMileage() {
        return KEY_VEH_MILEAGE;
    }

    public static String getKeyImei() {
        return KEY_IMEI;
    }

    public static String getKeyVehPhNo() {
        return KEY_VEH_PH_NO;
    }

    public static String getKeyVehicleNo() {
        return KEY_VEHICLE_NO;
    }


    public static String getVehicleDetails() {
        return T_VEHICE_DETAILS;
    }

    public static String getKeyLongitude() {
        return KEY_LONGITUDE;
    }

    public static String getKeyLatitude() {
        return KEY_LATITUDE;
    }

    public static String getKeyLocName() {
        return KEY_LOC_NAME;
    }

    public static String getKeyDistance() {
        return KEY_DISTANCE;
    }

    public static String getKeyLocationtype() {
        return KEY_LOCATIONTYPE;
    }

    public static String getLocation() {
        return T_LOCATION;
    }


    public static String getInboxDetails() {
        return T_INBOX;
    }

    public static String getKeyAdvanceType() {
        return KEY_ADVANCE_TYPE;
    }

    public static String getRegistrationMail() {
        return KEY_REG_MAIL;
    }

    public static String getRegistrationPin() {
        return KEY_PIN;
    }

    public static String getTableRegistration() {
        return T_REG;
    }

    public static String getRegistrationIs() {
        return KEY_ISREG;
    }

    public static String getKeyDeduction() {
        return KEY_DEDUCTION;
    }

    public static String getKeyManagetype() {
        return KEY_MANAGETYPE;
    }

    public static String getKeyAddr() {
        return KEY_ADDR;
    }

    public static String getKeyLicNo() {
        return KEY_LIC_NO;
    }

    public static String getAdvanceDetails() {
        return T_ADVANCE_DETAILS;
    }

    public static String getKeyEmployeeId() {
        return KEY_EMPLOYEE_ID;
    }

    public static String getKeySalary() {
        return KEY_SALARY;
    }

    public static String getKeyPhNo() {
        return KEY_PHNO;
    }

    public static String getKeyFuelRowId() {

        return KEY_FUELROWID;
    }

    public static String getKeyEmployeeRowId() {

        return KEY_EMPLOYEE_ID;
    }

    public static String getKeyProductKey() {

        return KEY_PRODUCTKEY;
    }

    public static String getKeyUserType() {

        return KEY_USERTYPE;
    }

    public static String getKeySubject() {

        return KEY_SUBJECT;
    }

    public static String getKeyMessage() {

        return KEY_MESSAGE;
    }

     public static String getKeyFlag() {
        return KEY_FLAG;
    }

    public static String getKeyClientname() {
        return KEY_CLIENTNAME;
    }

    public static String getKeyId() {
        return KEY_ROWID;
    }

    public static String getKeySign(){
        return KEY_SIGN;
    }

    public static String getKeyReceipt(){
        return KEY_RECEIPT;
    }


    //   public  static String getKeyParticular(){return  KEY_PARTICULAR; }

    //  public static String getExpensedetails(){return T_EXPENSEDETAILS;}




    @SuppressLint("NewApi")
    @SuppressWarnings("static-access")
    // @SuppressLint({ "NewApi"})
    public void onTerminate() {
        File f = new
                File("/data/data/com.example.anand_roadwayss/databases/MyDB5");//Give
        //Database Name Here
        db.deleteDatabase(f);

    }

    public Cursor retrieveAllData() {
        Cursor cursor = db.query(T_REG, null, null, null,
                null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();

        }
        return cursor;
    }


}
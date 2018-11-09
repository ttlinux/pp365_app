package org.sex.hanker.Utils.VideoDownload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/31.
 */
public class VideoSQL extends SQLiteOpenHelper {

    public static final int NotYetFinish = 0;
    public static final int Finished = 1;
    public static final int NewFile = 2;
    public static final int ERROR = -1;
    public static final int Pause = -2;
    private static final String LocalVideoDataBase = "LocalVideoDataBase.db";
    private static final String TABLE = "LocalVideo";
    private static final String M3U8ITEM_TABLE = "M3u8ItemTable";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_URL = "URL";
    private static final String VIDEO_TITLE = "VIDEO_TITLE";
    private static final String VIDEO_ID = "VIDEO_ID";
    private static final String COUNTRY = "COUNTRY";
    private static final String SUFFIX = "SUFFIX";
    private static final String STATUS = "STATUS";//0未完成 1已完成 -1错误
    private static final String LocalPath = "LocalPath";
    private static final String Persent = "Persent";
    private static final String TS_URL = "TS_URL";
    private static final String M3U8_URL = "M3U8_URL";
    private static final String FILE_INDEX = "FILE_INDEX";
    private static final String Parent_ID = "Parent_ID";
    private static final String VIDEO_PHOTO = "VIDEO_PHOTO";
    private static final String TimeLineUrl="TimeLineUrl";
    private static final String TimeLineImageType="TimeLineImageIype";
    private static final String TimeLineCount="TimeLineCount";

    public static VideoSQL videoSQL;
    public static SQLiteDatabase sqLiteDatabase;

    private static final String CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    VIDEO_ID + " TEXT UNIQUE NOT NULL," +
                    Persent + " INTEGER ," +
                    COLUMN_URL + " TEXT NOT NULL," +
                    SUFFIX + " TEXT NOT NULL," +
                    COUNTRY + " TEXT NOT NULL," +
                    LocalPath + " TEXT NOT NULL," +
                    STATUS + " INTEGER ," +
                    VIDEO_TITLE + " TEXT NOT NULL," +
                    VIDEO_PHOTO + " TEXT ," +
                    TimeLineUrl + " TEXT ," +
                    TimeLineImageType + " INTEGER ," +
                    TimeLineCount + " INTEGER " +
                    ") ;";

    private static final String CREATE_M3U8_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " + M3U8ITEM_TABLE +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            M3U8_URL + " TEXT NOT NULL," +
            TS_URL + " TEXT NOT NULL," +
            SUFFIX + " TEXT NOT NULL," +
            STATUS + " INTEGER," +
            FILE_INDEX + " INTEGER," +
            Parent_ID + " INTEGER," +
            LocalPath + " TEXT NOT NULL" +
            ") ;";

    public VideoSQL(Context context) {
        super(context, LocalVideoDataBase, null, 1);
    }

    public VideoSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
        db.execSQL(CREATE_M3U8_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized static SparseArray<BroadcastDataBean> getColumnData(boolean isDone)
    {
        SparseArray<BroadcastDataBean> beans=new SparseArray<>();
        StringBuilder sb=new StringBuilder();
        sb.append("select * from ");
        sb.append(TABLE);
        sb.append(" where ");
        sb.append(STATUS);
        if(isDone)
        {
            sb.append(" = ");
        }
        else
        {
            sb.append(" != ");
        }
        sb.append(Finished);
        Cursor cursor = sqLiteDatabase.rawQuery(sb.toString(), null);
        if (cursor.getCount() == 0) {
        } else {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                BroadcastDataBean bean = new BroadcastDataBean();
                bean.setCOLUMN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                bean.setCOUNTRY(cursor.getString(cursor.getColumnIndex(COUNTRY)));
                bean.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                bean.setLocalPath(cursor.getString(cursor.getColumnIndex(LocalPath)));
                bean.setSTATUS(cursor.getInt(cursor.getColumnIndex(STATUS)));
                bean.setSUFFIX(cursor.getString(cursor.getColumnIndex(SUFFIX)));
                bean.setVIDEO_ID(cursor.getString(cursor.getColumnIndex(VIDEO_ID)));
                bean.setPersent(cursor.getInt(cursor.getColumnIndex(Persent)));
                bean.setVIDEO_TITLE(cursor.getString(cursor.getColumnIndex(VIDEO_TITLE)));
                bean.setVIDEO_PHOTO(cursor.getString(cursor.getColumnIndex(VIDEO_PHOTO)));
                bean.setTimeLineUrl(cursor.getString(cursor.getColumnIndex(TimeLineUrl)));
                bean.setTimeLineCount(cursor.getInt(cursor.getColumnIndex(TimeLineCount)));
                bean.setTimeLineImageIype(cursor.getInt(cursor.getColumnIndex(TimeLineImageType)));
                beans.put(bean.getID(),bean);
            }
        }
        cursor.close();
        return beans;
    }

    public synchronized static LocalVideoBean getColumnData(Context context, String phid, String country) {
//        String SQL="select * from "+TABLE+" where "+VIDEO_ID+" == '"+phid+"'";
        String SQL = "select * from %s where %s = '%s' and %s='%s'";
        SQL = String.format(SQL, TABLE, VIDEO_ID, phid, COUNTRY, country);
        Cursor cursor = sqLiteDatabase.rawQuery(SQL, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            LocalVideoBean bean = new LocalVideoBean();
            bean.setCOLUMN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
            bean.setCOUNTRY(cursor.getString(cursor.getColumnIndex(COUNTRY)));
            bean.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            bean.setLocalPath(cursor.getString(cursor.getColumnIndex(LocalPath)));
            bean.setSTATUS(cursor.getInt(cursor.getColumnIndex(STATUS)));
            bean.setSUFFIX(cursor.getString(cursor.getColumnIndex(SUFFIX)));
            bean.setVIDEO_ID(cursor.getString(cursor.getColumnIndex(VIDEO_ID)));
            bean.setPersent(cursor.getInt(cursor.getColumnIndex(Persent)));
            bean.setVIDEO_TITLE(cursor.getString(cursor.getColumnIndex(VIDEO_TITLE)));
            bean.setVIDEO_PHOTO(cursor.getString(cursor.getColumnIndex(VIDEO_PHOTO)));
            bean.setTimeLineUrl(cursor.getString(cursor.getColumnIndex(TimeLineUrl)));
            bean.setTimeLineCount(cursor.getInt(cursor.getColumnIndex(TimeLineCount)));
            bean.setTimeLineImageIype(cursor.getInt(cursor.getColumnIndex(TimeLineImageType)));
            cursor.close();
            return bean;
        }
    }

    public synchronized static ArrayList<LocalVideoBean> selectNotFinish(Context context) {
        ArrayList<LocalVideoBean> localVideoBeans = new ArrayList<>();
        String SQL = "select * from " + TABLE + " where " + STATUS + " !=" + Finished;
        Cursor cursor = sqLiteDatabase.rawQuery(SQL, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            LocalVideoBean bean = new LocalVideoBean();
            bean.setCOLUMN_URL(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
            bean.setCOUNTRY(cursor.getString(cursor.getColumnIndex(COUNTRY)));
            bean.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            bean.setLocalPath(cursor.getString(cursor.getColumnIndex(LocalPath)));
            bean.setSTATUS(cursor.getInt(cursor.getColumnIndex(STATUS)));
            bean.setSUFFIX(cursor.getString(cursor.getColumnIndex(SUFFIX)));
            bean.setVIDEO_ID(cursor.getString(cursor.getColumnIndex(VIDEO_ID)));
            bean.setPersent(cursor.getInt(cursor.getColumnIndex(Persent)));
            localVideoBeans.add(bean);
        }
        cursor.close();
        return localVideoBeans;
    }

    public synchronized static void insertData(Context context, LocalVideoBean bean) {
        String SQL = "INSERT OR IGNORE INTO " + TABLE + " (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) " +
                "VALUES ('%s','%s','%s',%d,'%s','%s',%d,'%s','%s','%s',%d,%d)";
        SQL = String.format(SQL, COLUMN_URL, COUNTRY, LocalPath, STATUS,
                SUFFIX, VIDEO_ID,Persent,VIDEO_TITLE,
                VIDEO_PHOTO,TimeLineUrl,TimeLineImageType,TimeLineCount,
                bean.getCOLUMN_URL(), bean.getCOUNTRY(), bean.getLocalPath(), bean.getSTATUS(),
                bean.getSUFFIX(), bean.getVIDEO_ID(), bean.getPersent(),bean.getVIDEO_TITLE(),
                bean.getVIDEO_PHOTO(),bean.getTimeLineUrl(),bean.getTimeLineImageIype(),bean.getTimeLineCount());
        sqLiteDatabase.execSQL(SQL);
    }

    public synchronized static void updateSingleColumn(Context context, LocalVideoBean bean) {
        String sqlstr = "UPDATE %s set %s=%d,%s='%s',%s=%d,%s='%s',%s='%s',%s='%s',%s=%d,%s=%d WHERE %s='%s' and %s='%s'";
        sqLiteDatabase.execSQL(String.format(sqlstr, TABLE, STATUS, bean.getSTATUS(), LocalPath, bean.getLocalPath(),
                Persent, bean.getPersent(), COLUMN_URL, bean.getCOLUMN_URL(), VIDEO_PHOTO, bean.getVIDEO_PHOTO(),
                TimeLineUrl, bean.getTimeLineUrl(), TimeLineImageType, bean.getTimeLineImageIype(), TimeLineCount, bean.getTimeLineCount(),
                VIDEO_ID, bean.getVIDEO_ID(), COUNTRY, bean.getCOUNTRY()));
    }

    public synchronized static void delateSingleColumn( LocalVideoBean bean) {
        String sqlstr = "DELETE from %s where %s='%s'";
        sqLiteDatabase.execSQL(String.format(sqlstr, TABLE, VIDEO_ID, bean.getVIDEO_ID()));
    }

    public synchronized static void rebuildTable(Context context) {
        try {
//            db.execSQL("DROP TABLE "+TABLE);
//            db.execSQL(CREATE_SQL);
        } catch (SQLiteException ex) {
            LogTools.e(TABLE, TABLE + "不存在");
        }
    }

    public synchronized static LocalVideoBean getColumnData_M3U8(Context context, String phid, String country) {
        LocalVideoBean bean = getColumnData(context, phid, country);
        if (bean != null) {
            String SQL = "select * from %s where %s = %d ORDER BY %s ASC";
            SQL = String.format(SQL, M3U8ITEM_TABLE, Parent_ID, bean.getID(), FILE_INDEX);
            Cursor cursor = sqLiteDatabase.rawQuery(SQL, null);
            ArrayList<LocalVideoBean.M3U8_ITEM> m3U8_items = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                LocalVideoBean.M3U8_ITEM m3u8 = new LocalVideoBean.M3U8_ITEM();
                m3u8.setFILE_INDEX(cursor.getInt(cursor.getColumnIndex(FILE_INDEX)));
                m3u8.setID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                m3u8.setLocalPath(cursor.getString(cursor.getColumnIndex(LocalPath)));
                m3u8.setM3U8_URL(cursor.getString(cursor.getColumnIndex(M3U8_URL)));
                m3u8.setParent_ID(cursor.getInt(cursor.getColumnIndex(Parent_ID)));
                m3u8.setSTATUS(cursor.getInt(cursor.getColumnIndex(STATUS)));
                m3u8.setTS_URL(cursor.getString(cursor.getColumnIndex(TS_URL)));
                m3u8.setSUFFIX(cursor.getString(cursor.getColumnIndex(SUFFIX)));
                m3U8_items.add(m3u8);
            }
            bean.setM3U8_items(m3U8_items);
            cursor.close();
        }
        return bean;
    }

    public synchronized static void UpdateM3U8Item(Context context, LocalVideoBean.M3U8_ITEM m3U8_item) {
        String SQLSTR = "UPDATE %s set %s=%d,%s=%d,%s='%s',%s='%s',%s='%s',%s='%s' WHERE %s='%s'";
        SQLSTR = String.format(SQLSTR, M3U8ITEM_TABLE, STATUS, m3U8_item.getSTATUS(), FILE_INDEX, m3U8_item.getFILE_INDEX(), M3U8_URL, m3U8_item.getM3U8_URL(),
                TS_URL, m3U8_item.getTS_URL(), LocalPath, m3U8_item.getLocalPath(), SUFFIX, m3U8_item.getSUFFIX(), Parent_ID, m3U8_item.getParent_ID());
        sqLiteDatabase.execSQL(SQLSTR);
    }

    public synchronized static void InsertM3U8Item_Range(Context context, ArrayList<LocalVideoBean.M3U8_ITEM> m3U8_items) {
        sqLiteDatabase.beginTransaction();
        for (int i = 0; i < m3U8_items.size(); i++) {
            LocalVideoBean.M3U8_ITEM mi = m3U8_items.get(i);
            String SQLSTR = "INSERT OR IGNORE INTO " + M3U8ITEM_TABLE + " (%s,%s,%s,%s,%s,%s,%s) " +
                    "VALUES ('%s','%s','%s',%d,%d,%d,'%s')";

            SQLSTR = String.format(SQLSTR, M3U8_URL, TS_URL, SUFFIX, STATUS, FILE_INDEX, Parent_ID, LocalPath, mi.getM3U8_URL(), mi.getTS_URL(), mi.getSUFFIX(), mi.getSTATUS()
                    , mi.getFILE_INDEX(),
                    mi.getParent_ID(), mi.getLocalPath());
            sqLiteDatabase.execSQL(SQLSTR);
        }
        sqLiteDatabase.endTransaction();
    }


    public synchronized static void DeleteM3U8Item(Context context, int parent_id) {
        String sqlstr = "DELETE from %s where %s=%d";
        sqlstr = String.format(sqlstr, M3U8ITEM_TABLE, Parent_ID, parent_id);
        sqLiteDatabase.execSQL(sqlstr);
    }

    public static void InitSQL(BaseApplication baseApplication)
    {
        videoSQL = new VideoSQL(baseApplication);
        sqLiteDatabase = videoSQL.getWritableDatabase();
    }

    public static void Close()
    {
        if(videoSQL!=null)
        {
            videoSQL.close();
        }
        if(sqLiteDatabase!=null)
        {
            sqLiteDatabase.close();
        }
    }
}

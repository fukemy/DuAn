package com.example.macos.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "DATA_TYPE_ITEM".
*/
public class DataTypeItemDao extends AbstractDao<DataTypeItem, Void> {

    public static final String TABLENAME = "DATA_TYPE_ITEM";

    /**
     * Properties of entity DataTypeItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property DataID = new Property(0, Long.class, "DataID", false, "DATA_ID");
        public final static Property DataType = new Property(1, Integer.class, "DataType", false, "DATA_TYPE");
        public final static Property MaDuong = new Property(2, Integer.class, "MaDuong", false, "MA_DUONG");
        public final static Property TuyenSo = new Property(3, Integer.class, "TuyenSo", false, "TUYEN_SO");
        public final static Property MoTaTinhTrang = new Property(4, String.class, "MoTaTinhTrang", false, "MO_TA_TINH_TRANG");
        public final static Property KinhDo = new Property(5, String.class, "KinhDo", false, "KINH_DO");
        public final static Property ViDo = new Property(6, String.class, "ViDo", false, "VI_DO");
        public final static Property CaoDo = new Property(7, String.class, "CaoDo", false, "CAO_DO");
        public final static Property NguoiNhap = new Property(8, String.class, "NguoiNhap", false, "NGUOI_NHAP");
        public final static Property ThoiGianNhap = new Property(9, String.class, "ThoiGianNhap", false, "THOI_GIAN_NHAP");
        public final static Property ThangDanhGia = new Property(10, String.class, "ThangDanhGia", false, "THANG_DANH_GIA");
        public final static Property DataUUID = new Property(11, String.class, "dataUUID", false, "DATA_UUID");
    };


    public DataTypeItemDao(DaoConfig config) {
        super(config);
    }
    
    public DataTypeItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DATA_TYPE_ITEM\" (" + //
                "\"DATA_ID\" INTEGER," + // 0: DataID
                "\"DATA_TYPE\" INTEGER," + // 1: DataType
                "\"MA_DUONG\" INTEGER," + // 2: MaDuong
                "\"TUYEN_SO\" INTEGER," + // 3: TuyenSo
                "\"MO_TA_TINH_TRANG\" TEXT," + // 4: MoTaTinhTrang
                "\"KINH_DO\" TEXT," + // 5: KinhDo
                "\"VI_DO\" TEXT," + // 6: ViDo
                "\"CAO_DO\" TEXT," + // 7: CaoDo
                "\"NGUOI_NHAP\" TEXT," + // 8: NguoiNhap
                "\"THOI_GIAN_NHAP\" TEXT," + // 9: ThoiGianNhap
                "\"THANG_DANH_GIA\" TEXT," + // 10: ThangDanhGia
                "\"DATA_UUID\" TEXT);"); // 11: dataUUID
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DATA_TYPE_ITEM\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DataTypeItem entity) {
        stmt.clearBindings();
 
        Long DataID = entity.getDataID();
        if (DataID != null) {
            stmt.bindLong(1, DataID);
        }
 
        Integer DataType = entity.getDataType();
        if (DataType != null) {
            stmt.bindLong(2, DataType);
        }
 
        Integer MaDuong = entity.getMaDuong();
        if (MaDuong != null) {
            stmt.bindLong(3, MaDuong);
        }
 
        Integer TuyenSo = entity.getTuyenSo();
        if (TuyenSo != null) {
            stmt.bindLong(4, TuyenSo);
        }
 
        String MoTaTinhTrang = entity.getMoTaTinhTrang();
        if (MoTaTinhTrang != null) {
            stmt.bindString(5, MoTaTinhTrang);
        }
 
        String KinhDo = entity.getKinhDo();
        if (KinhDo != null) {
            stmt.bindString(6, KinhDo);
        }
 
        String ViDo = entity.getViDo();
        if (ViDo != null) {
            stmt.bindString(7, ViDo);
        }
 
        String CaoDo = entity.getCaoDo();
        if (CaoDo != null) {
            stmt.bindString(8, CaoDo);
        }
 
        String NguoiNhap = entity.getNguoiNhap();
        if (NguoiNhap != null) {
            stmt.bindString(9, NguoiNhap);
        }
 
        String ThoiGianNhap = entity.getThoiGianNhap();
        if (ThoiGianNhap != null) {
            stmt.bindString(10, ThoiGianNhap);
        }

        String ThangDanhGia = entity.getThangDanhGia();
        if (ThangDanhGia != null) {
            stmt.bindString(11, ThangDanhGia);
        }
 
        String dataUUID = entity.getDataUUID();
        if (dataUUID != null) {
            stmt.bindString(12, dataUUID);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public DataTypeItem readEntity(Cursor cursor, int offset) {
        DataTypeItem entity = new DataTypeItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // DataID
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // DataType
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // MaDuong
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // TuyenSo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // MoTaTinhTrang
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // KinhDo
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // ViDo
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // CaoDo
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // NguoiNhap
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // ThoiGianNhap
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // ThangDanhGia
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // dataUUID
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DataTypeItem entity, int offset) {
        entity.setDataID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDataType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setMaDuong(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setTuyenSo(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setMoTaTinhTrang(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setKinhDo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setViDo(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCaoDo(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setNguoiNhap(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setThoiGianNhap(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setThangDanhGia(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDataUUID(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(DataTypeItem entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(DataTypeItem entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

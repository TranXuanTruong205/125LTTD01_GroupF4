package com.dinerestaurant.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý session của user, bao gồm trạng thái đặt bàn hiện tại
 * 
 * Table reservation sẽ được xóa khi:
 * - User đăng xuất
 * - User hoàn thành đặt hàng
 */
public class TableSessionManager {

    private static final String PREF_NAME = "TableSession";
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_HAS_TABLE = "has_table";

    private static TableSessionManager instance;
    private final SharedPreferences prefs;

    private TableSessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TableSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new TableSessionManager(context);
        }
        return instance;
    }

    /**
     * Lưu thông tin bàn khi quét QR hoặc đặt bàn thành công
     */
    public void saveTableReservation(String tableId, String tableName) {
        prefs.edit()
                .putString(KEY_TABLE_ID, tableId)
                .putString(KEY_TABLE_NAME, tableName)
                .putBoolean(KEY_HAS_TABLE, true)
                .apply();
    }

    /**
     * Kiểm tra có đang có bàn hay không
     */
    public boolean hasTableReservation() {
        return prefs.getBoolean(KEY_HAS_TABLE, false);
    }

    /**
     * Lấy table ID
     */
    public String getTableId() {
        return prefs.getString(KEY_TABLE_ID, null);
    }

    /**
     * Lấy table name
     */
    public String getTableName() {
        return prefs.getString(KEY_TABLE_NAME, null);
    }

    /**
     * Xóa thông tin bàn (khi đăng xuất hoặc hoàn thành order)
     */
    public void clearTableReservation() {
        prefs.edit()
                .remove(KEY_TABLE_ID)
                .remove(KEY_TABLE_NAME)
                .putBoolean(KEY_HAS_TABLE, false)
                .apply();
    }

    /**
     * Xóa tất cả session (khi đăng xuất)
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}

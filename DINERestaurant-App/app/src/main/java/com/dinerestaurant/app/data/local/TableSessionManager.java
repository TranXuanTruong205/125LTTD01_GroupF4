package com.dinerestaurant.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý session đặt bàn và phương thức order
 * 
 * Session sẽ được xóa khi:
 * - User đăng xuất
 * - User hoàn thành đặt hàng
 */
public class TableSessionManager {

    private static final String PREF_NAME = "TableSession";

    // Table info (từ quét QR)
    private static final String KEY_TABLE_ID = "table_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_HAS_TABLE = "has_table";

    // Selected order type (từ MyLocations)
    private static final String KEY_ORDER_TYPE = "order_type"; // dine_in, takeaway, delivery
    private static final String KEY_DISPLAY_ADDRESS = "display_address";
    private static final String KEY_ADDRESS_ID = "address_id";
    private static final String KEY_HAS_SELECTION = "has_selection";

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

    // =====================================================
    // TABLE RESERVATION (từ QR scan)
    // =====================================================

    /**
     * Lưu thông tin bàn khi quét QR
     */
    public void saveTableReservation(String tableId, String tableName) {
        prefs.edit()
                .putString(KEY_TABLE_ID, tableId)
                .putString(KEY_TABLE_NAME, tableName)
                .putBoolean(KEY_HAS_TABLE, true)
                .apply();
    }

    public boolean hasTableReservation() {
        return prefs.getBoolean(KEY_HAS_TABLE, false);
    }

    public String getTableId() {
        return prefs.getString(KEY_TABLE_ID, null);
    }

    public String getTableName() {
        return prefs.getString(KEY_TABLE_NAME, null);
    }

    // =====================================================
    // ORDER SELECTION (từ MyLocations)
    // =====================================================

    /**
     * Lưu phương thức order đã chọn
     * 
     * @param orderType:      "dine_in", "takeaway", "delivery"
     * @param displayAddress: Text hiển thị trên Cart (VD: "Bàn 1", "Đến quán lấy",
     *                        "123 ABC...")
     * @param addressId:      ID địa chỉ (nếu là delivery)
     */
    public void saveOrderSelection(String orderType, String displayAddress, int addressId) {
        prefs.edit()
                .putString(KEY_ORDER_TYPE, orderType)
                .putString(KEY_DISPLAY_ADDRESS, displayAddress)
                .putInt(KEY_ADDRESS_ID, addressId)
                .putBoolean(KEY_HAS_SELECTION, true)
                .apply();
    }

    /**
     * Lưu phương thức order (không có addressId)
     */
    public void saveOrderSelection(String orderType, String displayAddress) {
        saveOrderSelection(orderType, displayAddress, -1);
    }

    public boolean hasOrderSelection() {
        return prefs.getBoolean(KEY_HAS_SELECTION, false);
    }

    public String getOrderType() {
        return prefs.getString(KEY_ORDER_TYPE, null);
    }

    public String getDisplayAddress() {
        return prefs.getString(KEY_DISPLAY_ADDRESS, null);
    }

    public int getAddressId() {
        return prefs.getInt(KEY_ADDRESS_ID, -1);
    }

    // =====================================================
    // CLEAR
    // =====================================================

    /**
     * Xóa thông tin bàn (sau khi hoàn thành order)
     */
    public void clearTableReservation() {
        prefs.edit()
                .remove(KEY_TABLE_ID)
                .remove(KEY_TABLE_NAME)
                .putBoolean(KEY_HAS_TABLE, false)
                .apply();
    }

    /**
     * Xóa selection (sau khi hoàn thành order)
     */
    public void clearOrderSelection() {
        prefs.edit()
                .remove(KEY_ORDER_TYPE)
                .remove(KEY_DISPLAY_ADDRESS)
                .remove(KEY_ADDRESS_ID)
                .putBoolean(KEY_HAS_SELECTION, false)
                .apply();
    }

    /**
     * Xóa tất cả (khi đăng xuất hoặc hoàn thành order)
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}

package com.sournary.contentproviderwithroom.data.source.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.sournary.contentproviderwithroom.data.model.Cheese
import com.sournary.contentproviderwithroom.data.source.room.CheeseDatabase

/**
 * Created: 30/08/2018
 * By: Sang
 * Description:
 */
class CheeseProvider : ContentProvider() {

    /**
     * Initialize ContentProvider on main thread + at app launch time + not perform lengthy operations.
     * If you perform lengthy operation, app launch time will be delayed.
     * Avoid perform: open, update or scan database until CP is used via query or insert.
     * If you use SQLiteOpenHelper, avoid call SQLiteHelperOpenHelper.getReadableDatabase() or getWritableDatabase()
     *
     * @return: true if CP is loaded successfully, false otherwise.
     */
    override fun onCreate(): Boolean = true

    /**
     * @param uri: Full Uri to query sent by the client.
     * @param projection: List of columns to put into the Cursor. Passing "null" then all column will be included
     * @param selection: A selection criteria to apply when filtering rows. Passing "null" then all rows will be included.
     * @param selectionArgs: You may include "?" in "selection" params. These "?" will be replaced by selectionArgs in order.
     * @param sortOrder: the rows in the cursors should be sorted. Passing "null" then "provider" will free to define the sort order. Normally is increase
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? =
        context?.let {
            val cheeseDao = CheeseDatabase.getInstance(it).cheeseDao()
            when (MATCHER.match(uri)) {
                CODE_CHEESE_PROVIDER_ITEM -> cheeseDao.selectById(ContentUris.parseId(uri))
                CODE_CHEESE_PROVIDER_DIR -> cheeseDao.selectedAll()
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }

    /**
     * Insert new data to Content Provider.
     * Call contentResolve.notifyChange() sau khi insert
     *
     * @param uri: content:// URI của insertion request
     * @param contentValues: 1 tập các cặp column_name/value thêm vào trong database
     *
     * @return: Uri của item được thêm mới vào.
     */
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? =
        context?.let {
            when (MATCHER.match(uri)) {
                CODE_CHEESE_PROVIDER_DIR -> {
                    val id = CheeseDatabase.getInstance(it)
                        .cheeseDao()
                        .insert(Cheese.fromContentValues(contentValues))
                    it.contentResolver.notifyChange(uri, null)
                    return@let ContentUris.withAppendedId(uri, id)
                }
                CODE_CHEESE_PROVIDER_ITEM -> throw IllegalArgumentException("Invalid Uri, can not insert with ID: $uri")
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }

    /**
     * Update existing data trong content provider.
     * Calling notifyChange() after calling update()
     *
     * @param uri: Uri to query. This is
     */
    override fun update(
        uri: Uri, value: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int =
        if (context != null) {
            when (MATCHER.match(uri)) {
                CODE_CHEESE_PROVIDER_DIR -> throw IllegalArgumentException("Invalid Uri, cannot update without id: $uri")
                CODE_CHEESE_PROVIDER_ITEM -> {
                    val cheese = Cheese.fromContentValues(value)
                    cheese.id = ContentUris.parseId(uri)
                    val count = CheeseDatabase.getInstance(context!!).cheeseDao().update(cheese)
                    context!!.contentResolver.notifyChange(uri, null)
                    count
                }
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        } else {
            0
        }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        if (context != null) {
            when (MATCHER.match(uri)) {
                CODE_CHEESE_PROVIDER_DIR -> throw IllegalArgumentException("Invalid Uri, cannot delete with uri: $uri")
                CODE_CHEESE_PROVIDER_ITEM -> {
                    val count = CheeseDatabase.getInstance(context!!)
                        .cheeseDao()
                        // ContentUris.parseId(): Convert the last path segment to a long; -1 if the path is empty
                        .deleteById(ContentUris.parseId(uri))
                    context!!.contentResolver.notifyChange(uri, null)
                    count
                }
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        } else {
            0
        }

    /**
     * Trả về kiểu MIME của dữ liệu trong ContentProvider
     * Bắt đầu bằng: vnd.android.cursor.item - đối với single record; vnd.android.cursor.dir - đối với multiple items
     *
     */
    override fun getType(uri: Uri): String =
        when (MATCHER.match(uri)) {
            CODE_CHEESE_PROVIDER_DIR -> "vnd.android.cursor.dir/$AUTHORITY.${Cheese.TABLE_NAME}"
            CODE_CHEESE_PROVIDER_ITEM -> "vnd.android.cursor.item/$AUTHORITY.${Cheese.TABLE_NAME}"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    companion object {

        /** The authority of this content provider */
        private const val AUTHORITY = "com.sournary.contentproviderwithroom.provider"

        /** The match code for some items in the Cheese table */
        private const val CODE_CHEESE_PROVIDER_DIR = 1

        /** The match code for an items in the Cheese table */
        private const val CODE_CHEESE_PROVIDER_ITEM = 2

        val URI_CHEESE = Uri.parse("content://$AUTHORITY/${Cheese.TABLE_NAME}")

        /**
         * Add Uri to match. The code is used to check whenever the Uri is matched.
         * Use * for any text, # for only numbers.
         *
         * @param authority: the authority to match. (quyền để khớp)
         * @param path: The path to match. May use * for any text, # for only number
         * @param code: The code that is return when a Uri is matched against the given components.
         */
        val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, Cheese.TABLE_NAME, CODE_CHEESE_PROVIDER_DIR)
            addURI(AUTHORITY, Cheese.TABLE_NAME + "/*", CODE_CHEESE_PROVIDER_ITEM)
        }
    }
}
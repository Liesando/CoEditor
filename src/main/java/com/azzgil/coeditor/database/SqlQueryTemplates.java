package com.azzgil.coeditor.database;

import com.azzgil.coeditor.database.dbmeta.DBColumns;
import com.azzgil.coeditor.database.dbmeta.DBTables;

public class SqlQueryTemplates {
    public static final String INSERT_DOCUMENT_PREPARED = "INSERT INTO "
            + DBTables.DOCUMENTS + "(" + DBColumns.DOCUMENT_NAME +
            ") VALUES(?)";

    public static final String INSERT_DOCUMENT_VERSION_PREPARED = "INSERT INTO "
            + DBTables.DOCUMENT_VERSIONS + " VALUES(?,?,?,?)";

    public static final String SELECT_MAX_DOCUMENT_ID = "SELECT MAX(" + DBColumns.DOCUMENT_ID +
            ") FROM " + DBTables.DOCUMENTS;

    public static final String SELECT_ALL_DOCUMENTS = "SELECT * FROM " + DBTables.DOCUMENTS;

    public static String selectLastVersionOfDocument(int id) {
        return "SELECT dv." + DBColumns.DOCUMENT_ID +
                ", d." + DBColumns.DOCUMENT_NAME +
                ", dv." + DBColumns.MODIFICATION_TIME +
                ", dv." + DBColumns.DOCUMENT_DATA +
                " FROM " + DBTables.DOCUMENT_VERSIONS + " AS dv" +
                " INNER JOIN " + DBTables.DOCUMENTS + " AS d" +
                " ON dv." + DBColumns.DOCUMENT_ID + "=d." + DBColumns.DOCUMENT_ID +
                " WHERE dv." + DBColumns.DOCUMENT_ID +
                "=" + id +
                " AND " + DBColumns.MODIFICATION_TIME +
                "=(SELECT MAX(" + DBColumns.MODIFICATION_TIME +
                ") FROM " + DBTables.DOCUMENT_VERSIONS +
                " WHERE " + DBColumns.DOCUMENT_ID +
                "=" + id + ")";
    }

}

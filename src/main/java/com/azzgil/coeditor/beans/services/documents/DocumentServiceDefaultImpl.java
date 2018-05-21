package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.database.SqlQueryTemplates;
import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.utils.logging.ColoredLogger;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumentServiceDefaultImpl implements DocumentService {

    private ColoredLogger logger = new ColoredLogger(DocumentServiceDefaultImpl.class.getCanonicalName());
    private DBService dbService;

    @Autowired
    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public boolean createDocument(Document document) throws SQLException {

        try (PreparedStatement ps = dbService.getConnection()
                .prepareStatement(SqlQueryTemplates.INSERT_DOCUMENT_PREPARED)) {
//            ps.setInt(1, document.getId()); - autoincremented
            ps.setString(1, document.getName());

            int result = ps.executeUpdate();

            logResult(result);

            // create a brand new version of document
            return result == 1 && updateDocument(document);
        }
    }

    private void logResult(int resultCode) {
        if (resultCode != 1) {
            logger.error("The result is assumed to be 1, but got " + resultCode + " instead");
        } else {
            logger.info("Successfully updated document");
        }
    }

    private Clob makeNClobOf(String data) throws SQLException {
        Clob result = dbService.getConnection().createClob();
        result.setString(1, data);
        return result;
    }

    @Override
    public Document getDocumentById(int id) throws SQLException {
        try (Statement stm = dbService.getConnection().createStatement()) {
            ResultSet rs = stm.executeQuery(SqlQueryTemplates.selectLastVersionOfDocument(id));
            if (rs.next()) {
                return parse(rs);
            } else {
                return null;
            }
        }
    }

    private Document parse(ResultSet rs) throws SQLException {
        Document document = new Document();

        // 1 - id, 2 - name, 3 - modification time, 4 - data
        document.setId(rs.getInt(1));
        document.setName(rs.getString(2));
        document.setData(getDataFrom(rs.getNClob(4)));
        return document;
    }

    private String getDataFrom(NClob nClob) throws SQLException {
        String data = nClob.getSubString(1, (int) nClob.length());
        nClob.free();
        return data;
    }

    @Override
    public boolean updateDocument(Document document) throws SQLException {
        try (PreparedStatement ps = dbService.getConnection()
                .prepareStatement(SqlQueryTemplates.INSERT_DOCUMENT_VERSION_PREPARED)) {
            ps.setInt(1, document.getId());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            Clob clob = makeNClobOf(document.getData());
            ps.setClob(3, clob);
            ps.setString(4, document.getVersionLabel());

            int result = ps.executeUpdate();
            clob.free();

            logResult(result);

            return result == 1;
        }
    }

    @Override
    public boolean deleteDocument(Document document) {
        throw new NotImplementedException();
    }
}

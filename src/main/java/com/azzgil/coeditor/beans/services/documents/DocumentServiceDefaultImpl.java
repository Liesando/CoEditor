package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.database.SqlQueryTemplates;
import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.utils.logging.ColoredLogger;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                .prepareStatement(SqlQueryTemplates.INSERT_DOCUMENT_PREPARED);
             Statement stm = dbService.getConnection().createStatement()) {
            ps.setString(1, document.getName());

            int result = ps.executeUpdate();

            logResult(result);

            // if we didn't succeed there's no need to continue
            if (result != 1) {
                return false;
            }

            // we need to extract the id of newly created document
            // and put it in the document variable.
            // we can achieve this by selecting the maximal value
            // of DOCUMENT_ID column, since the last added id is
            // always maximal
            ResultSet rs = stm.executeQuery(SqlQueryTemplates.SELECT_MAX_DOCUMENT_ID);

            if (rs.next()) {
                document.setId(rs.getInt(1));
            } else {
                // fatal error, should never happen
                throw new RuntimeException();
            }

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
    public Document getDocumentById(int id) throws SQLException, DocumentVersionNotFoundException {
        try (Statement stm = dbService.getConnection().createStatement()) {
            ResultSet rs = stm.executeQuery(SqlQueryTemplates.selectLastVersionOfDocument(id));
            if (rs.next()) {
                return parse(rs);
            } else {
                throw new DocumentVersionNotFoundException();
            }
        }
    }

    private Document parse(ResultSet rs) throws SQLException {

        // 1 - id, 2 - name, 3 - modification time, 4 - data
        Document document = parseDocumentOnly(rs);
        document.setLastModification(rs.getTimestamp(3).toLocalDateTime());
        document.setData(getDataFrom(rs.getNClob(4)));
        return document;
    }

    private Document parseDocumentOnly(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setId(rs.getInt(1));
        document.setName(rs.getString(2));
        return document;
    }

    private String getDataFrom(Clob clob) throws SQLException {
        String data = clob.getSubString(1, (int) clob.length());
        clob.free();
        return data;
    }

    @Override
    public List<Document> getAllDocuments() throws SQLException {
        try (Statement stm = dbService.getConnection().createStatement()) {

            ResultSet rs = stm.executeQuery(SqlQueryTemplates.SELECT_ALL_DOCUMENTS);
            List<Document> documents = new ArrayList<>();
            while (rs.next()) {
                documents.add(parseDocumentOnly(rs));
            }

            return documents;
        }
    }

    @Override
    public boolean updateDocument(Document document) throws SQLException {
        try (PreparedStatement ps = dbService.getConnection()
                .prepareStatement(SqlQueryTemplates.INSERT_DOCUMENT_VERSION_PREPARED)) {

            document.setLastModification(LocalDateTime.now());
            ps.setInt(1, document.getId());
            ps.setTimestamp(2, Timestamp.valueOf(document.getLastModification()));
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

        // we don't need this possibility yet
        throw new NotImplementedException();
    }
}

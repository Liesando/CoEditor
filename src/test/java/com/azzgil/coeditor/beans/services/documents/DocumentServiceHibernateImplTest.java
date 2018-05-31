package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.configs.AppConfig;
import com.azzgil.coeditor.configs.DatabaseConfig;
import com.azzgil.coeditor.model.DocumentVersion;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, DatabaseConfig.class})
@WebAppConfiguration
public class DocumentServiceHibernateImplTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DocumentVersionService documentVersionService;

    @Test
    public void test_getDocumentService() {
        DocumentVersionService documentVersionService = applicationContext.getBean(DocumentVersionService.class);
        Assert.assertNotNull(documentVersionService);
    }

    @Test
    public void test_getDocumentVersion() throws Exception {
        DocumentVersion documentVersion = documentVersionService.getLastVersionOf(1);
        Assert.assertTrue("wrong doc name", documentVersion.getDocument().getName().equals("test"));
        Assert.assertTrue("wrong doc data", documentVersion.getData().equals("test data"));
        Assert.assertTrue("wrong doc version label", documentVersion.getVersionLabel().equals("initial version"));
    }
}

package org.sakaiproject.scorm.service.tincanapi.impl.util;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.Setter;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.w3c.dom.Document;

public class TinCanAPIDocumentUtils {

    @Setter
    private ContentHostingService contentHostingService;

    /**
     * Creates a document from a given resource path
     * 
     * @param resourcePath the path to the content resource
     * @return the created {@link Document} object
     */
    public Document parseResourceAsDocument(String resourcePath) {
        Document document = null;
        ContentResource contentResource = null;
        try {
            contentResource = contentHostingService.getResource(resourcePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
              builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            document = builder.parse(new ByteArrayInputStream(contentResource.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }
}

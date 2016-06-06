package org.sakaiproject.articulate.tincan.util;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class ArticulateTCDocumentUtils {

    private final Logger log = LoggerFactory.getLogger(ArticulateTCDocumentUtils.class);

    @Setter
    private ContentHostingService contentHostingService;

    /**
     * Creates a document from a given resource path
     * 
     * @param resourcePath the path to the content resource
     * @return the created {@link Document} object
     */
    public Document parseResourceAsDocument(String resourcePath) {
        if (StringUtils.isBlank(resourcePath)) {
            throw new IllegalArgumentException("Resource path cannot be null.");
        }

        Document document = null;
        ContentResource contentResource = null;
        try {
            contentResource = contentHostingService.getResource(resourcePath);
        } catch (Exception e) {
            log.error("Error getting resource as document from path: {}", resourcePath, e);
        }

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            log.error("Error setting document builder object.", e);
        }

        try {
            document = builder.parse(new ByteArrayInputStream(contentResource.getContent()));
        } catch (Exception e) {
            log.error("Error parsing content resource stream.", e);
        }

        return document;
    }

}

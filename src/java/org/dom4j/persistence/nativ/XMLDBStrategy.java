package org.dom4j.persistence.nativ;

import java.net.URL;

import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.persistence.DocumentMarshalling;
import org.dom4j.persistence.MarshallingContext;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;

import org.xmldb.api.base.Database;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.SAXResource;
import org.xmldb.api.modules.TransactionService;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 *
 * @author Tobias Rademacher
 * @version 1.0
 */

public class XMLDBStrategy extends DocumentMarshalling {

  protected SAXContentHandler extractContent;
  protected SAXWriter resolver;
  protected MarshallingContext context;
  protected TransactionService transactionService;
  protected Collection collection;

  public XMLDBStrategy(MarshallingContext context) throws IllegalAccessException, java.lang.InstantiationException {
    this.context = context;
    Database database = (Database) this.context.getDatabaseDriver().newInstance();
    this.collection = DatabaseManager.getCollection(this.context.getDatabaseLocation().toExternalForm());
    TransactionService transaction =
    (TransactionService) this.collection.getService("TransactionService", "1.0");
  }

  public void marshal(String aId, Node aNode) throws Exception {
     this.resolve(aNode);
     SAXResource resource = (SAXResource) this.collection.createResource(aId, "SAXResource");
     resource.setContentHandler(this.extractContent);
     this.collection.storeResource(resource);
     if(this.context.isAutoCommiting())
       transactionService.commit();
  }

  public Node unmarshal(String systemId) {
    XMLResource resource = (XMLResource) this.collection.getResource(systemId);
    resource.getContentAsSAX(this.extractContent);
    return this.extractContent.getDocument();
  }

  protected void resolve(Node aNode) throws SAXException {
    this.extractContent = new SAXContentHandler();
    this.resolver = new SAXWriter(this.extractContent);
    this.resolver.write((Document)aNode);
  }

   public void setContext(MarshallingContext aContext) {
    this.context = aContext;
   }


}

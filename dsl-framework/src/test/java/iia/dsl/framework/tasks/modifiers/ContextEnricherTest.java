package iia.dsl.framework.tasks.modifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import iia.dsl.framework.core.Message;
import iia.dsl.framework.core.Slot;
import iia.dsl.framework.util.TestUtils;

/**
 * Tests unitarios para ContextEnricher.
 */
public class ContextEnricherTest {
    
    // Mensaje de contexto que indica dónde enriquecer (/xpath) y qué añadir (/body)
    private static final String CONTEXT_XML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <context>
                <xpath>/order/items</xpath>
                <body><item><productId>P003</productId><name>Keyboard</name><price>49.99</price><quantity>1</quantity></item></body>
            </context>
            """;

    @Test
    public void testEnrichesMessageWithContext() throws Exception {
        Document mainDoc = TestUtils.createXMLDocument(TestUtils.SAMPLE_XML);
        Document contextDoc = TestUtils.createXMLDocument(CONTEXT_XML);

        Slot input = new Slot("in");
        Slot context = new Slot("context");
        Slot output = new Slot("out");

        ContextEnricher enricher = new ContextEnricher("ce-1", input, context, output);

        Message mainMessage = new Message("msg-123", mainDoc);
        Message contextMessage = new Message("ctx-123", contextDoc);
        
        input.setMessage(mainMessage);
        context.setMessage(contextMessage);

        enricher.execute();

        Message out = output.getMessage();
        assertNotNull(out, "Output message must not be null");
        assertNotNull(out.getDocument(), "Document should be present on the output message");
        
        NodeList items = out.getDocument().getElementsByTagName("item");
        assertEquals(3, items.getLength(), "Should have 2 original items from SAMPLE_XML plus 1 enriched item from context");
        
        boolean keyboardFound = false;
        for (int i = 0; i < items.getLength(); i++) {
            var item = items.item(i);
            var children = item.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                if ("name".equals(children.item(j).getNodeName()) && 
                    "Keyboard".equals(children.item(j).getFirstChild().getNodeValue())) {
                    keyboardFound = true;
                    break;
                }
            }
        }
        assertTrue(keyboardFound, "Enriched item (Keyboard) should be present in the output");
    }
}
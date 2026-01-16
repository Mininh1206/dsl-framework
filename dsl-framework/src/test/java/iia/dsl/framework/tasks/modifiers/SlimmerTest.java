package iia.dsl.framework.tasks.modifiers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import iia.dsl.framework.core.Message;
import iia.dsl.framework.core.Slot;
import iia.dsl.framework.util.TestUtils;

public class SlimmerTest {

    @Test
    public void testSlimmerRemovesNode() throws Exception {
        Document doc = TestUtils.createXMLDocument(TestUtils.SAMPLE_XML);

        Slot inputSlot = new Slot("input");
        Slot outputSlot = new Slot("output");

        Slimmer slimmer = new Slimmer("test-slimmer", inputSlot, outputSlot, "/order/header");
        
        inputSlot.setMessage(new Message(doc));

        slimmer.execute();

        Document result = outputSlot.getMessage().getDocument();
        assertNotNull(result, "Output document should not be null");

        Node headerNode = result.getElementsByTagName("header").item(0);
        assertNull(headerNode, "Header node should be removed");

        Node itemsNode = result.getElementsByTagName("items").item(0);
        assertNotNull(itemsNode, "Items node should still exist");
    }
}

package edu.upc.fib.masd.jav.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationParser {
    public static Map<String, Map<String, Integer>> readConfiguration(String path) {
        Map<String, Map<String, Integer>> configuration = new HashMap<>();
        try {
            File file = new File(path);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();


            configuration.put("baron", ConfigurationParser.readConfiguration(doc, "baron"));
            configuration.put("villager", ConfigurationParser.readConfiguration(doc, "villager"));

            configuration.put("init", ConfigurationParser.readConfiguration(doc, "init"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return configuration;
    }

    private static Map<String, Integer> readConfiguration(Document doc, String type) {
        Map<String, Integer> configuration = new HashMap<>();
        Node node = doc.getElementsByTagName(type).item(0);

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;

            if ("init".equals(type)) {
                // Initialization settings
                configuration.put("barons", Integer.valueOf(element.getElementsByTagName("barons").item(0).getTextContent()));
                configuration.put("collectors", Integer.valueOf(element.getElementsByTagName("collectors").item(0).getTextContent()));
                configuration.put("builders", Integer.valueOf(element.getElementsByTagName("builders").item(0).getTextContent()));
            } else {
                // Basic configurations
                configuration.put("startFood", Integer.valueOf(element.getElementsByTagName("startFood").item(0).getTextContent()));
                configuration.put("startWood", Integer.valueOf(element.getElementsByTagName("startWood").item(0).getTextContent()));
                configuration.put("startFoodSatiety", Integer.valueOf(element.getElementsByTagName("startFoodSatiety").item(0).getTextContent()));
                configuration.put("maxFood", Integer.valueOf(element.getElementsByTagName("maxFood").item(0).getTextContent()));
                configuration.put("maxWood", Integer.valueOf(element.getElementsByTagName("maxWood").item(0).getTextContent()));
                configuration.put("giveValue", Integer.valueOf(element.getElementsByTagName("giveValue").item(0).getTextContent()));

                // Field configurations
                Element field = (Element) element.getElementsByTagName("field").item(0);
                if (field != null) {
                    configuration.put("numFields", Integer.valueOf(field.getElementsByTagName("numFields").item(0).getTextContent()));
                    configuration.put("startFieldYield", Integer.valueOf(field.getElementsByTagName("startFieldYield").item(0).getTextContent()));
                    configuration.put("minYield", Integer.valueOf(field.getElementsByTagName("minYield").item(0).getTextContent()));
                    configuration.put("sownRounds", Integer.valueOf(field.getElementsByTagName("sownRounds").item(0).getTextContent()));
                    configuration.put("increaseYieldRounds", Integer.valueOf(field.getElementsByTagName("increaseYieldRounds").item(0).getTextContent()));
                }

                // Build configurations
                Element buildCost = (Element) element.getElementsByTagName("woodRequiredToBuild").item(0);
                if (buildCost != null) {
                    configuration.put("woodRequiredToBuild", Integer.valueOf(buildCost.getTextContent()));
                }
            }
        }

        return configuration;
    }

}
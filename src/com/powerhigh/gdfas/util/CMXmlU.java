package com.powerhigh.gdfas.util;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Description: xml工具类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
 */

public class CMXmlU
 {

    /**
     * 功能说明
     *
     * @param root 说明
     * @param tagName 说明
     * @param subTagName 说明
     * @param attribute 说明
     *
     * @return 说明
     */
    public static String getSubTagAttribute(Element root, String tagName,
        String subTagName, String attribute)
     {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);

        for (int loop = 0; loop < list.getLength(); loop++)
         {
            Node node = list.item(loop);

            if (node != null)
             {
                NodeList children = node.getChildNodes();

                for (int innerLoop = 0; innerLoop < children.getLength();
                        innerLoop++)
                 {
                    Node child = children.item(innerLoop);

                    if ((child != null) && (child.getNodeName() != null) &&
                            child.getNodeName().equals(subTagName))
                     {
                        if (child instanceof Element)
                         {
                            return ((Element) child).getAttribute(attribute);
                         }
                     }
                 }
             }
         }

        return returnString;
     }

    /**
     * 功能说明
     *
     * @param node 说明
     * @param subTagName 说明
     *
     * @return 说明
     */
    public static String getSubTagValue(Node node, String subTagName)
     {
        String returnString = "";

        if (node != null)
         {
            NodeList children = node.getChildNodes();

            for (int innerLoop = 0; innerLoop < children.getLength();
                    innerLoop++)
             {
                Node child = children.item(innerLoop);

                if ((child != null) && (child.getNodeName() != null) &&
                        child.getNodeName().equals(subTagName))
                 {
                    Node grandChild = child.getFirstChild();

                    if (grandChild.getNodeValue() != null)
                     {
                        return grandChild.getNodeValue();
                     }
                 }
             }
         }

        return returnString;
     }

    /**
     * 功能说明
     *
     * @param root 说明
     * @param tagName 说明
     * @param subTagName 说明
     *
     * @return 说明
     */
    public static String getSubTagValue(Element root, String tagName,
        String subTagName)
     {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);

        for (int loop = 0; loop < list.getLength(); loop++)
         {
            Node node = list.item(loop);

            if (node != null)
             {
                NodeList children = node.getChildNodes();

                for (int innerLoop = 0; innerLoop < children.getLength();
                        innerLoop++)
                 {
                    Node child = children.item(innerLoop);

                    if ((child != null) && (child.getNodeName() != null) &&
                            child.getNodeName().equals(subTagName))
                     {

                        Node grandChild = child.getFirstChild();

                        if (grandChild.getNodeValue() != null)
                         {
                            return grandChild.getNodeValue();
                         }
                     }
                 }
             }
         }

        return returnString;
     }

    /**
     * 功能说明
     *
     * @param root 说明
     * @param tagName 说明
     * @param attribute 说明
     *
     * @return 说明
     */
    public static String getTagAttribute(Element root, String tagName,
        String attribute)
     {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);

        for (int loop = 0; loop < list.getLength(); loop++)
         {
            Element element = (Element) list.item(loop);
            return element.getAttribute(attribute);
         }

        return returnString;
     }

    /**
     * 功能说明
     *
     * @param root 说明
     * @param tagName 说明
     *
     * @return 说明
     */
    public static String getTagValue(Element root, String tagName)
     {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);

        for (int loop = 0; loop < list.getLength(); loop++)
         {
            Node node = list.item(loop);

            if (node != null)
             {
                Node child = node.getFirstChild();

                if ((child != null) && (child.getNodeValue() != null))
                 {
                    return child.getNodeValue();
                 }
             }
         }

        return returnString;
     }

    /**
     * 功能说明
     *
     * @param url 说明
     *
     * @return 说明
     *
     * @throws Exception 说明
     */
    public static Element loadDocument(URL url) throws Exception
     {
        Document doc = null;

        try
         {
            InputSource xmlInp = new InputSource(url.openStream());
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = docBuilderFactory.newDocumentBuilder();
            doc = parser.parse(xmlInp);

            Element root = doc.getDocumentElement();
            root.normalize();
            return root;
         }catch (SAXParseException e)
         {
            e.printStackTrace();
            throw e;
         }catch (SAXException e)
         {
            e.printStackTrace();
            throw e;
         }catch (MalformedURLException e)
         {
            e.printStackTrace();
            throw e;
         }catch (IOException e)
         {
            e.printStackTrace();
            throw e;
         }catch (Exception e)
         {
            e.printStackTrace();
            throw e;
         }
     }
 }

package com.powerhigh.gdfas.util;

import java.net.URL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Description: xml��Դ�� <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier��
 * Modify Time��
 */

public class CMXmlR
 {
    private static URL resoucesURL;
    private static Element root;
  
    public static String getResource(String as_section, String as_tag)
     {
        if (root == null)
        {
            init();
         }
        return CMXmlU.getTagAttribute(root, as_section, as_tag);
     }


    public static Element getResourceRoot()
     {
        if (root == null)
        {
            init();
         }

        return root;
     }
    
    public static NodeList getElementsByTagName(String tag_name){
    	if (root == null)
        {
            init();
         }
    	return root.getElementsByTagName(tag_name);
    }
  
    public static void init()
     {
        try
         {
        	resoucesURL = CMXmlR.class.getClassLoader().getResource(CMConfig.CONFIG_FILE);

         }catch (Exception E)
         {
            throw new RuntimeException("���ܴ���Դ�ļ�:" + CMConfig.CONFIG_FILE + "!",E);
         }

        try
         {
            root = CMXmlU.loadDocument(resoucesURL);
         }catch (Exception E)
         {
            throw new RuntimeException("��ȡ��Դ�ļ�:" + CMConfig.CONFIG_FILE + "����!",E);
         }
     }

   
    public static void main(String[] args)
     {

        
     }
 }

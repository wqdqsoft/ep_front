package com.powerhigh.gdfas.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class SerializableObject  implements Serializable{
  public  byte   data[];
  public boolean SaveObject(Object obj){

     if ( Serializable.class.isInstance(obj)){
       try {
         ByteArrayOutputStream out    =    new ByteArrayOutputStream();
         ObjectOutputStream    stream =    new ObjectOutputStream(out);
         stream.writeObject(obj);
         stream.close();
         this.data = out.toByteArray();
         out.close();
       } catch(Exception e){
         e.printStackTrace();
          return false;
       }
     }
    return true;
  }
   
  public Object LoadObject(){
    Object obj= null;
     ByteArrayInputStream in=null;
     ObjectInputStream stream=null;
    try {
       in = new ByteArrayInputStream(data);
       stream = new ObjectInputStream(in);
       obj = stream.readObject();
       stream.close();
       in.close();
    } catch(Exception e)
    {
      e.printStackTrace();
      return null;
    };
    return obj;
  }
  public static Object clone(Object obj){
    SerializableObject sol= new SerializableObject();
    sol.SaveObject(obj);
    return sol.LoadObject();
  }
}
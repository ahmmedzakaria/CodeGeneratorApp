/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generate.mapper;


import java.util.HashMap;
import java.util.Map;

import model.FieldMaps;


/**
 *
 * @author Zakaria
 */
public class SqlToJavaMapper implements FieldMapper{
	
   private static Map<String, FieldMaps> fieldMapJava = new HashMap();
    static {
       fieldMapJava.put("integer", new FieldMaps("int"));
       fieldMapJava.put("character varying", new FieldMaps("String"));
       fieldMapJava.put("text", new FieldMaps("String"));
       fieldMapJava.put("double precision",  new FieldMaps("double"));
       fieldMapJava.put("float",  new FieldMaps("double"));
       fieldMapJava.put("timestamp without time zone",  new FieldMaps("Timestamp","java.sql.Timestamp"));
       fieldMapJava.put("uuid",  new FieldMaps("UUID", "java.util.UUID"));
       fieldMapJava.put("boolean", new FieldMaps("boolean"));
       fieldMapJava.put("date", new FieldMaps("Date","java.sql.Date"));
       fieldMapJava.put("bigint", new FieldMaps("long"));
    }
    
    @Override
    public FieldMaps getDataType(String sqlDataType){   
       // System.out.println("sqlDataType: "+sqlDataType+" "+fieldMapJava.get(sqlDataType).getDataType());
    	//System.out.println("java: "+fieldMapJava.size());
        return fieldMapJava.get(sqlDataType);
    }
}

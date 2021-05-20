package generate.mapper;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.HashMap;
import java.util.Map;

import model.FieldMaps;



/**
 *
 * @author Zakaria
 */
public class SqlToDartMapper implements FieldMapper{
	
    private static Map<String, FieldMaps> fieldMapDart = new HashMap();
    static {
        fieldMapDart.put("integer", new FieldMaps("int"));
        fieldMapDart.put("character varying", new FieldMaps("String"));
        fieldMapDart.put("character varying(255)", new FieldMaps("String"));
        fieldMapDart.put("text", new FieldMaps("String"));
        fieldMapDart.put("character", new FieldMaps("String"));
        fieldMapDart.put("double precision",  new FieldMaps("double"));
        fieldMapDart.put("float",  new FieldMaps("double"));
        fieldMapDart.put("timestamp without time zone",  new FieldMaps("dynamic"));
        fieldMapDart.put("uuid",  new FieldMaps("String"));
        fieldMapDart.put("UUID",  new FieldMaps("String"));
        fieldMapDart.put("boolean", new FieldMaps("dynamic"));
        fieldMapDart.put("date", new FieldMaps("dynamic"));
        fieldMapDart.put("bigint", new FieldMaps("int"));
        fieldMapDart.put("real", new FieldMaps("double"));
        fieldMapDart.put("geography(Point,4326)", new FieldMaps("String"));
    }
    
    @Override
    public FieldMaps getDataType(String sqlDataType){   
    	//System.out.println("sqlDataType: "+sqlDataType+" "+fieldMapDart.get(sqlDataType).getDataType());
    	//System.out.println("dart: "+fieldMapDart.size());
        return fieldMapDart.get(sqlDataType);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generate;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import generate.reader.SqlRawData;
import model.ClassField;
import model.JavaAnnotation;
import model.ProcessedData;
import util.C;


/**
 *
 * @author Zakaria
 */
public class DartClassGenerator  implements ClassGenerator{

	
		List<Path> files ;
	    String wordSeperator;
	    private StringBuffer classContent = new StringBuffer();
	    private StringBuffer imports = new StringBuffer();
	    
	    Predicate<String> isDate = s -> s.equals("date") || s.equals("timestamp without time zone");
	    Predicate<String> isBoolean = s -> s.equals("boolean"); 
	    Predicate<String> isNotNull = s -> s != null; 
	    
		@Override
		public ProcessedData getModelClassData(SqlRawData rawData) {
			ProcessedData processData = new ProcessedData();
			   processData.setContent(getModelClassContent(rawData));
			   processData.setFileName(rawData.getClassName());
			   processData.setFileExt(C.DART_EXT);
			   processData.setDestinationPath(C.PRODUCT_PATH);
			return processData;
		}
	    
	    
	    private StringBuffer getModelClassContent(SqlRawData rawData) {
	       	System.out.println("Generating Table: "+rawData.getTableName()+ ", Field Size: "+rawData.getFieldList().size());
	       	
	       	ClassField.importList.clear();
	       	JavaAnnotation.importList.clear();
	       	rawData.getFieldList().forEach(field->{
	               if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
	               //	addTopCommentIfExist(field,classContent);
	               //	addAnnotationsIfExist(field);
	            
	               	addField(field);
	               //	addRightCommentIfExist(field);
	               	
	                    
	                               
	               }
	           });
	       	
	        classContent.append("\n\n");  
	       	
	        generateConstructor(rawData);
	        generateToMap(rawData);
	        generateFromMap(rawData);
	        generateToJSon(rawData);
	        generateFromJSon(rawData);
	        
			return classContent;
	           
	       } 
  
	    private void addField(ClassField field) {
	       	if(field.getFieldName().equals("id")) {
        		classContent
            	.append("\t").append("dynamic").append(" ")
            	.append(field.getFieldName()).append(";\n");   
        	}else {

            	classContent
            	.append("\t").append(field.getDataType()).append(" ")
            	.append(field.getFieldName()).append(";\n"); 
        	} 
	    }

    
    void generateConstructor(SqlRawData rawData) {
    	classContent.append("\n").append(rawData.getClassName()).append("(){}");
    }
    

    
    public void  generateToMap(SqlRawData rawData){
	//	System.out.println("generateToMap: ");
		classContent.append("\n\n");
		classContent.append("Map<String, dynamic> toMap(){")
		.append("\n")
		.append("\tvar map = <String, dynamic>{")
		.append("\n");
		
		rawData.getFieldList().forEach(field->{
		            if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
		            	classContent
		            	.append("\t'").append(field.getSqlFieldName()).append("' : ");
		            	
		            	//System.out.println(field.getSqlDataType());
		            	
		            	if(field.getSqlDataType()!=null && isDate.test(field.getSqlDataType())) {
		            		//System.out.println("ToMap Date");
		            		classContent
			            	.append("\"")
			            	.append("${Utility.getDateAsMiliSecond(")
			            	.append(field.getFieldName())
			            	.append(".toString())}")
			            	.append("\"")
			            	.append(",\n");
		            	}else if(isNotNull.and(isBoolean).test(field.getSqlDataType())){
		            		classContent
			            	.append("\"")
			            	.append("${Utility.boolToInt(")
			            	.append(field.getFieldName())
			            	.append(")}")
			            	.append("\"")
			            	.append(",\n");
		            	}else {

			            	classContent
			            	.append(field.getFieldName()).append(",\n");
		            	}   
		                
		            }
		        });
		 classContent.append("\t};\n")
		 .append("return map;\n")
		 .append("}");
    }
    
    public void  generateFromMap(SqlRawData rawData){
 		//System.out.println("generateFromMap: ");
 		classContent.append("\n\n")
  		.append("")
  		.append(rawData.getClassName())
 		.append(".fromMap( Map<String, dynamic> map){\n");
 		rawData.getFieldList().forEach(field->{
 		            if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
 		            	classContent
 		            	.append("\t")
 		            	.append(field.getFieldName())
 		            	.append(" = ");
 		            	if(isNotNull.and(isDate).test(field.getSqlDataType())) {
 		            		classContent
	 		            	.append("Utility.getFormatedDateFromMiliSecond(map['")
			            	.append(field.getSqlDataType())
			            	.append("'])")
	 		            	.append(";\n");
		            	}else if(isNotNull.and(isBoolean).test(field.getSqlDataType())){
		            		classContent
	 		            	.append("Utility.intToBool(map['")
			            	.append(field.getSqlDataType())
			            	.append("'])")
	 		            	.append(";\n");
		            	}else {

		            		classContent
	 		            	.append("map['").append(field.getSqlFieldName()).append("']")
	 		            	.append(";\n"); 
		            	} 
 		            	  
 		                
 		            }
 		        });
 		        
 		classContent.append("}\n");
     }
    
    
    public void  generateToJSon(SqlRawData rawData){
		//System.out.println("generateToJSon: ");
		classContent.append("\n\n");
		classContent.append("String toJson(){")
		.append("\n")
		.append("\tString str ='{';")
		.append("\n");
		
		rawData.getFieldList().forEach(field->{
		            if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
		            	if(field.getFieldName().equals("id")) {
		            		classContent
			            	.append("\t//str += '").append("\"").append(field.getFieldName()).append("\" : \"$")
			            	.append(field.getFieldName()).append("\",';\n"); 
		            	}else {
		            		classContent
			            	.append("\tstr += '").append("\"").append(field.getFieldName()).append("\" : \"$")
			            	.append(field.getFieldName()).append("\",';\n"); 
		            	}
		            	 
		                
		            }
		        });
		 classContent.append("\tstr+='}';\n")
		 .append("return str;\n")
		 .append("}");
    }
    
    public void  generateFromJSon(SqlRawData rawData){
  		//System.out.println("generateFromJSon: ");
  		classContent.append("\n\n")
  		.append("")
  		.append(rawData.getClassName())
  		.append(".fromJson(Map<String, dynamic> map){\n");
  		rawData.getFieldList().forEach(field->{
//  		            if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
//  		            	if(field.getDataType()!=null && field.getSqlDataType().equals("boolean")) {
//  		  		    		if(field.getFieldName()!=null && field.getFieldName().substring(0, 2).equals("is")) {
//  		  		    			classContent
//  		  		            	.append("\t")
//  		  		            	.append(field.getFieldName())
//  		  		            	.append(" = map['")
//  		  		            	.append(getCamaleCase(field.getSqlFieldName().substring(3)))
//  		  		            	.append("']")
//  		  		            	.append(";\n");
//  		  		    			classContent.append(" set")
//  		  		            	.append(getPascalCase(field.getFieldName().substring(2)));
//  		  		    		}	
//  		  		    	}else {
  		            	classContent
  		            	.append("\t")
  		            	.append(field.getFieldName())
  		            	.append(" = map['").append(field.getFieldName()).append("']")
  		            	.append(";\n");
  		  		    //	}
  		                
  		           // }
  		            
  		          
  		        });

	            	
	    classContent.append("\n}\n");   
     }



    
    

    
 
}

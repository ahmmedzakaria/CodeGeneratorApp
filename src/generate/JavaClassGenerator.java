/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generate;


import model.ClassField;
import model.FieldMaps;
import model.JavaAnnotation;
import model.JavaCls;
import model.ProcessedData;
import util.C;
import util.FileWriterUtill;
import util.StringUtill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import generate.reader.SqlFileReader;
import generate.reader.SqlRawData;



/**
 *
 * @author Zakaria
 */
public class JavaClassGenerator implements ClassGenerator{
    
    List<Path> files ;
    String wordSeperator;
    private StringBuffer classContent;
    private StringBuffer imports = new StringBuffer();
    
    JavaClassGenerator(){
    	this.wordSeperator = SqlFileReader.wordSeperator;
    	this.classContent = new StringBuffer();
    }
    

   @Override
   public ProcessedData getModelClassData(SqlRawData rawData){
	   ProcessedData processData = new ProcessedData();
	   processData.setContent(getModelClassContent(rawData));
	   processData.setFileName(rawData.getClassName());
	   processData.setFileExt(C.JAVA_EXT);
	   processData.setDestinationPath(C.PRODUCT_PATH);
//        String fileName = rawData.getClassName();
//        String ext = ".java";
//        String path = "javaclasses";
//        String fileNameWithPath =path +File.separator+ rawData.getClassName() + ext;
//        System.out.println(fileNameWithPath);

		
//		  try { 
//			  FileWriterUtill.writeToFile(fileNameWithPath, getClassContent(rawData).toString()); 
//		  } catch(IOException e) { 
//			  e.printStackTrace(); 
//		  }
		return processData; 
    }
     
   
   private StringBuffer getModelClassContent(SqlRawData rawData) {
   	generateClassFields(rawData);
   	addClassLeveImportIfExsit(rawData);
       
       StringBuffer content = new StringBuffer();
       content.
       	append(imports).append("\n")
       	.append(getClassLevelAnnotationsIfExist(rawData))
	        .append("public class ").append(rawData.getClassName())
	        .append("{\n\n ").append(classContent).append("\n")
	        .append(getGeneratedGatterSetters(rawData))
	        .append("\n\n}");
       rawData.setClassContent(content);
      
    return content;
   }
   
   private void generateClassFields(SqlRawData rawData) {
   	System.out.println("Generating Table: "+rawData.getTableName()+ ", Field Size: "+rawData.getFieldList().size());
   	
   	ClassField.importList.clear();
   	JavaAnnotation.importList.clear();
   	rawData.getFieldList().forEach(field->{
           if(field.getFieldName()!=null && !field.getFieldName().contentEquals("")) {
           	addTopCommentIfExist(field,classContent);
           	addAnnotationsIfExist(field);
           	addField(field);
           	addRightCommentIfExist(field);
           	
               classContent.append("\n\n");    
                           
               addFieldImportsIfExist(field);
               addFieldAnnotationsImportsIfExist(field); 
               
           }
       });

       
   } 
    
   private StringBuilder getGeneratedGatterSetters(SqlRawData rawData) {
    	StringBuilder str = new StringBuilder();
    	rawData.getFieldList().forEach(field->{
    		str.append(getGeneratedGatter(field))
    		.append("\n\n")
    		.append(getGeneratedSetter(field))
    		.append("\n\n");
    	});
    	
    	return str;
    	
    }
    
    private void addAnnotationsIfExist(ClassField field) {

    	field.getAnnotatios().forEach(e->{
    		classContent
        	.append("\t").append(e.getAnnotation()).append("\n");
    	});
    }
    
    private StringBuffer getClassLevelAnnotationsIfExist(SqlRawData rawData) {
    	StringBuffer classAnnotation= new StringBuffer();
    	rawData.getJavaCls().getAnnotations().forEach(anno->{
    		classAnnotation.append(anno.getAnnotation()).append("\n");
        });
    	
   	return classAnnotation;
    }
    
    private void addTopCommentIfExist(ClassField field, StringBuffer classContent) {
    	if(field.getTopComment()!=null && field.getTopComment().length()>0) {
    		classContent
        	.append("\t").append(field.getTopComment()).append("\n");
    	}
    }
    
    private void addField(ClassField field) {
    	classContent
    	.append("\tprivate ").append(field.getDataType()).append(" ")
    	.append(field.getFieldName()).append(";");
    }
    
  private void addRightCommentIfExist(ClassField field) {
    	if(field.getRightComment() !=null) {
        	classContent.append(field.getRightComment());
        }
    }
    
   private void addFieldImportsIfExist(ClassField field) {
    	 int size=0;	
			size = ClassField.importList.stream()
         .filter(e->e.equals(field.getDataType()))
         .collect(Collectors.toList()).size();		
           
     if(size == 0 && field.getImportStr() != null) {
     	imports.append("import ").append(field.getImportStr()).append(";\n");
     	
     	ClassField.importList.add(field.getDataType());
     }
    }
    
   private void addFieldAnnotationsImportsIfExist(ClassField field) {
    	 if(field.getAnnotatios().size() > 0) {
         	field.getAnnotatios().forEach(annotation->{
         		int size2=0;
         		List<String> importStrList =annotation.getImportStrList();
         		
         		for(String importStr : importStrList) {
         			size2=JavaAnnotation.importList.stream()
    				 	.filter(a->a.equals(importStr))
    				 	.collect(Collectors.toList()).size();
         			if(size2 ==0) {
         				imports.append("import ").append(importStr).append(";\n");
             			JavaAnnotation.importList.add(importStr);
             		}
         		}
 				
         		
         	});                	
         }
    }
    
  private void addClassLeveImportIfExsit(SqlRawData rawData){
    	  rawData.getJavaCls().getAnnotations().forEach(anno->{
          	List<String> list = anno.getImportStrList();
          	list.forEach(imp->{
          		imports.append("import ").append(imp).append(";\n");
          	});        	
          });
    }
    
    
   private StringBuilder getGeneratedGatter(ClassField model) {
    	
    	StringBuilder str = new StringBuilder();
    	str.append("\tpublic ")
    	.append(model.getDataType()).append(" ");
    	if(model.getDataType()!=null && model.getDataType().equals("boolean")) {
        	str.append(model.getFieldName());
    		
    	}else {
        	str.append("get")
        	.append(StringUtill.getPascalCase(model.getFieldName(),wordSeperator));
    	}  	
    	
    	str.append("()")
    	.append("{\n")
    	.append("\t\treturn ")
    	.append("this.")
    	.append(model.getFieldName())
    	.append(";")
    	.append("\n\t}");
    	return str;
    }
    
   private StringBuilder getGeneratedSetter(ClassField model) {
    	StringBuilder str = new StringBuilder();
    	str.append("\tpublic void ");
    	if(model.getDataType()!=null && model.getDataType().equals("boolean")) {
    		if(model.getFieldName()!=null && model.getFieldName().substring(0, 2).equals("is")) {
    			str.append(" set")
            	.append(StringUtill.getPascalCase(model.getFieldName().substring(2),wordSeperator));
    		}
    		
    		
    	}else {
    		str.append(" set")
        	.append(StringUtill.getPascalCase(model.getFieldName(),wordSeperator));
    	} 
    	
    	str.append("(")
    	.append(model.getDataType())
    	.append(" ")
    	.append(model.getFieldName())
    	.append(")")
    	.append("{\n")
    	.append("\t\tthis.")
    	.append(model.getFieldName())
    	.append(" = ")
    	.append(model.getFieldName())
    	.append(";")
    	.append("\n\t}");
    	return str;
    	
    }
    
    
	
      
}

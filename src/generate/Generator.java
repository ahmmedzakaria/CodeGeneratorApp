/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import generate.mapper.FieldMapper;
import generate.mapper.SqlToDartMapper;
import generate.mapper.SqlToJavaMapper;
import generate.reader.SqlFileReader;
import generate.reader.SqlRawData;
import model.ProcessedData;
import util.C;
import util.FileUtill;
import util.FileWriterUtill;

/**
 *
 * @author Zakaria
 */
public class Generator {
    
	static SqlFileReader sqlFileReader = new SqlFileReader();
	static JavaClassGenerator javaClassGenerator =new JavaClassGenerator();
	static DartClassGenerator dartClassGenerator =new DartClassGenerator();
	static List<ProcessedData> filesToBeWrite = new ArrayList<>();
	
	static SqlToJavaMapper sqlToJavaMapper = new SqlToJavaMapper();
	static SqlToDartMapper sqlToDartMapper = new SqlToDartMapper();
	
    public static void main(String[] args) throws IOException {
    	String resourcePath=C.RESOURCE_PATH;
    	
    	
        
        final List<Path> files= FileUtill.getFilesFormDirectory(resourcePath);
        files.forEach(System.out::println);
        
        for (Path path : files) {
        	generateFile(javaClassGenerator,sqlToJavaMapper,path);
        	generateFile(dartClassGenerator,sqlToDartMapper,path);
        	
		}
       
        
        // Write to file System
        for (ProcessedData processedData : filesToBeWrite) {
        	writeToFile(processedData);
		}


    }
    
    
    static void generateFile(ClassGenerator classGenerator, FieldMapper fieldMapper, Path path) {
    	SqlRawData sqlRawData = sqlFileReader.getSQlRawData(path, fieldMapper);
    	ProcessedData processData = classGenerator.getModelClassData(sqlRawData);
    	filesToBeWrite.add(processData);
    }
    
    public static void writeToFile(ProcessedData processedData) {
      String fileNameWithPath =processedData.getDestinationPath() 
    		  + File.separator+ processedData.getFileName()
    		  + processedData.getFileExt();
	try { 
		  FileWriterUtill.writeToFile(fileNameWithPath, processedData.getContent().toString()); 
	  } catch(IOException e) { 
		  e.printStackTrace(); 
	  }
    }
}

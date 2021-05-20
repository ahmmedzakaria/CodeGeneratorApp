package generate.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import generate.mapper.FieldMapper;

import model.ClassField;
import model.FieldMaps;
import model.JavaAnnotation;
import model.JavaCls;
import util.StringUtill;

public class SqlFileReader {
	//private FieldMapper fieldMapper;
    private List<ClassField> fieldList;
    private StringBuffer stringBuffer = null;
    private String line=null;
    private String className;
    private String tableName;
    private StringBuffer topComment;
    private String[] skipFilter = {
    		"WITH",
    		"OIDS",
    		"TABLESPACE",
    		"CONSTRAINT",
    		"(",
    		")",
    		"ALTER",
    		"OWNER",
    		"DROP"
    };
    private int pos=0; 
    private JavaCls javaCls = new JavaCls();
    public static String wordSeperator = "_";   
    private SqlRawData sqlRawData;
    
 
    
    void prepareRawData(){
    	sqlRawData = new SqlRawData();
    	sqlRawData.setFieldList(fieldList);
    	sqlRawData.setClassName(className);
    	sqlRawData.setTableName(tableName);
    	sqlRawData.setJavaCls(javaCls);
//    	sqlRawData.setImports(imports);
//    	sqlRawData.setClassContent(classContent);
    	
    }
    
    public SqlRawData getSQlRawData(Path path, FieldMapper fieldMapper){
    	try {
			read(path,fieldMapper);
			prepareRawData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	return sqlRawData;
    }
    
	
    private void read(Path path, FieldMapper fieldMapper) throws FileNotFoundException, IOException{
        BufferedReader bufferedReader = new BufferedReader
                    (new FileReader(path.toString()));
        fieldList = new ArrayList();
        topComment = new StringBuffer();
        
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer = new StringBuffer(line);
                ClassField field = new ClassField();
                
                String line = stringBuffer.toString().trim().replaceFirst(",", "");
                String s=new String(line);
                List<String> strList = Arrays.asList(s.split("--"));
                if(strList.size() == 2) {
                	field.setRightComment("\t// "+strList.get(1).trim());
                	s=strList.get(0);
                }
                List<String> words = Arrays.asList(s.split(" "));
                if(skipUnwantedLines(words)) continue;
                
                if(words.get(0).substring(0, 2).equals("--")) {
                	topComment.append("// "+line+"\n");
                   continue;
                }
                  if(words.get(0).toUpperCase().equals("CREATE")) {
                	  tableName = words.get(2).replace("public.", "");
                      className = StringUtill.getPascalCase(tableName,wordSeperator);  
                   continue;
                  } 
                
                try {
                field.setSqlLine(s);
            	field.setFieldName(StringUtill.getCamelCase(words.get(0).trim(),wordSeperator));
                field.setSqlFieldName(words.get(0).trim());   
                generateAnnotationsForField(field);
                field.setSqlDataType(getSqlDataType(words));
                FieldMaps fieldMaps = fieldMapper.getDataType(field.getSqlDataType());
                System.out.println(field.getSqlFieldName());
               System.out.println(fieldMapper+" : "+fieldMaps.getDataType());
                field.setDataType(fieldMaps.getDataType());
                field.setImportStr(fieldMaps.getImportStr());
                generateTopAndRightComment(field,words);
                
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception: "+field);
                }
                fieldList.add(field);
            }
            bufferedReader.close();
            
            generateClassStr();
            
            for (int i = 0; i < 4; i++) {
            	System.out.println(fieldList.get(i).getSqlFieldName()+" : "+fieldList.get(i).getDataType());
			}
            
   
    }
    
    public String getSqlDataType(List<String> lineWords){
        List<String> words = new ArrayList(4);
        if(lineWords.size() > 2){
            words.add(lineWords.get(1).trim());
            words.add(lineWords.get(2).trim());
            if(words.get(0).equals("character") && words.get(1).equals("varying")){
            	pos=2;
                return new StringBuffer().append(words.get(0))
                    .append(" ").append(words.get(1))
                    .toString();
            }
            if(words.get(0).equals("double") && words.get(1).equals("precision")){
            	pos=2;
                return new StringBuffer().append(words.get(0))
                    .append(" ").append(words.get(1))
                    .toString();
            }
            
        }
        if (lineWords.size() > 4){
        	pos=4;
            words.add(lineWords.get(3).trim());
            words.add(lineWords.get(4).trim());
            if(words.get(0).equals("timestamp") && words.get(1).equals("without")){
                return new StringBuffer().append(words.get(0))
                    .append(" ").append(words.get(1))
                    .append(" ").append(words.get(2))
                    .append(" ").append(words.get(3))
                    .toString();
            }
            
        }
        pos=1;
        return lineWords.get(1);
    }
    
    boolean skipUnwantedLines(List<String> words) {
    	int size= Arrays.asList(skipFilter).stream()
              .filter(e->e.equals(words.get(0).toUpperCase()))
              .collect(Collectors.toList()).size();
        if(size>0) {
        	return true;
        } 
        if(words.size() <2) {
        	return true;
        }
        return false;
    }
    
    public List<String> getImportStrList(String ...strings){
    	List<String> list = new ArrayList();
    	Arrays.asList(strings).forEach(e->list.add(e));
    	return list;
    }
    
    void generateAnnotationsForField(ClassField field) {
        List<JavaAnnotation> annotationList =new ArrayList();               
        if(field.getFieldName().equals("id")) {
        	JavaAnnotation ann1 = new JavaAnnotation("@Id",getImportStrList("javax.persistence.Id"));
        	annotationList.add(ann1);
        	JavaAnnotation ann2 = new JavaAnnotation("@Type(type = \"org.hibernate.type.PostgresUUIDType\")",
        			getImportStrList("org.hibernate.annotations.Type"));
        	annotationList.add(ann2);
        	
        	JavaAnnotation ann3 = new JavaAnnotation("@GeneratedValue(strategy = GenerationType.AUTO)",
        			getImportStrList("javax.persistence.GeneratedValue","javax.persistence.GenerationType"));
        	annotationList.add(ann3);
        }
        
        String annotation ="@Column(name = \""+field.getSqlFieldName()+"\")";
        JavaAnnotation javaAnnotation = new JavaAnnotation();
        javaAnnotation.setAnnotation(annotation);
        javaAnnotation.setImportStrList(getImportStrList("javax.persistence.Column"));                
        annotationList.add(javaAnnotation);
        
        field.setAnnotations(annotationList); 
    }
    
    void generateTopAndRightComment(ClassField field,List<String> words) {

        if(field.getRightComment()!=null) {
        	field.setRightComment(getDefault(pos, words)+field.getRightComment());
        }else {
        	field.setRightComment(getDefault(pos, words));
        }
        
        if(topComment.length()>2) {
        	field.setTopComment(topComment.toString());
        	topComment=new StringBuffer();
        }
    }
    
    void generateClassStr() {
    	javaCls = new JavaCls();
        javaCls.setClassName(className);
        List<JavaAnnotation> annotationList =new ArrayList();    
    	JavaAnnotation ann1 = new JavaAnnotation("@Entity",getImportStrList("javax.persistence.Entity"));
    	annotationList.add(ann1);
    	String anno= "@Table(name = \""+tableName+"\")";
    	JavaAnnotation ann2 = new JavaAnnotation(anno,
    			getImportStrList("javax.persistence.Table"));
    	annotationList.add(ann2);
    	javaCls.setAnnotations(annotationList);
    }
    
    
    public String getDefault(int pos, List<String> lineWords) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\t//");
    	if(lineWords.size()>pos) {
    		for (int i=pos+1; i<lineWords.size(); i++) {
    			sb.append(lineWords.get(i)).append(" ");
    		}
    	}
    	if(sb.length()>3) return sb.toString();
    	return "";
    }
    
}

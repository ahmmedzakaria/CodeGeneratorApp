package generate.reader;

import java.util.List;
import model.ClassField;
import model.JavaCls;

public class SqlRawData {
	private String tableName;
	private String className;
	private StringBuffer classContent;
	private StringBuffer imports;
	private List<ClassField> fieldList;
    private JavaCls javaCls;
    
    
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public StringBuffer getClassContent() {
		return classContent;
	}
	public void setClassContent(StringBuffer classContent) {
		this.classContent = classContent;
	}
	public StringBuffer getImports() {
		return imports;
	}
	public void setImports(StringBuffer imports) {
		this.imports = imports;
	}
	public List<ClassField> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<ClassField> fieldList) {
		this.fieldList = fieldList;
	}
	public JavaCls getJavaCls() {
		return javaCls;
	}
	public void setJavaCls(JavaCls javaCls) {
		this.javaCls = javaCls;
	}

}

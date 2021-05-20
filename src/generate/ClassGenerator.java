package generate;

import generate.reader.SqlRawData;
import model.ProcessedData;

public interface ClassGenerator {
	public ProcessedData getModelClassData(SqlRawData rawData);
}

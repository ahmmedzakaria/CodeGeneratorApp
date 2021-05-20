package generate.mapper;

import model.FieldMaps;

public interface FieldMapper {
	public FieldMaps getDataType(String sqlDataType);
}

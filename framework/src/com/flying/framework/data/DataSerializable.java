package com.flying.framework.data;

public interface DataSerializable {
	//把对象系列化为Data对象
	default Data serialize() throws Exception{
		return DataUtils.serialize(this);
	}
	
	//把Data对象系列化为POJO对象
	@SuppressWarnings("unchecked")
	default <T extends DataSerializable>T deserialize(Data data) throws Exception{
		return (T)DataUtils.deserialize(data, this);
	}
}

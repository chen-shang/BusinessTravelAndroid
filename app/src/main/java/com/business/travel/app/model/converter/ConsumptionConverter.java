package com.business.travel.app.model.converter;

import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.model.ImageIconInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConsumptionConverter {
	ConsumptionConverter INSTANCE = Mappers.getMapper(ConsumptionConverter.class);

	ImageIconInfo convertImageIconInfo(Consumption consumption);
}

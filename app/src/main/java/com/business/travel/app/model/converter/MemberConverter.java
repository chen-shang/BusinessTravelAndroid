package com.business.travel.app.model.converter;

import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.model.ImageIconInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberConverter {
	MemberConverter INSTANCE = Mappers.getMapper(MemberConverter.class);

	ImageIconInfo convertImageIconInfo(Member member);
}

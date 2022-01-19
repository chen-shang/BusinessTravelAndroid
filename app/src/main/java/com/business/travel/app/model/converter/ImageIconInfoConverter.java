package com.business.travel.app.model.converter;

import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageIconInfoConverter {
    ImageIconInfoConverter INSTANCE = Mappers.getMapper(ImageIconInfoConverter.class);

    ImageIconInfo convertImageIconInfo(GiteeContent giteeContent);
}

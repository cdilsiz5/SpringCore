package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
  @Mapping(target = "trainings", ignore = true)
  TrainerDto toTrainerDto(Trainer trainer);

  @IterableMapping(elementTargetType = TrainerDto.class)
  List<TrainerDto> toTrainerDtoList(List<Trainer> trainers);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateTrainerRequest(UpdateTrainerRequest request, @MappingTarget Trainer trainer);

}

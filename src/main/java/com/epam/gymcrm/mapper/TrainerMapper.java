package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.request.trainer.CreateTrainerRequest;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

  TrainerDto toTrainerDto(Trainer trainer);

    List<TrainerDto> toTrainerDtoList(List<Trainer> trainers);

  Trainer createTrainer(CreateTrainerRequest request);

    void updateTrainerRequest(UpdateTrainerRequest request, @MappingTarget Trainer trainer);

}

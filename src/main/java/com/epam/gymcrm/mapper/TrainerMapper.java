package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.TrainerDto;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.request.trainer.UpdateTrainerRequest;

import java.util.List;

public interface TrainerMapper {
  TrainerDto toTrainerDto(Trainer trainer);

  List<TrainerDto> toTrainerDtoList(List<Trainer> trainers);

  void updateTrainerRequest(UpdateTrainerRequest request, Trainer trainer);

}

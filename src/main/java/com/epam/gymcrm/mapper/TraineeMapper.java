package com.epam.gymcrm.mapper;
import com.epam.gymcrm.dto.TraineeDto;
import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.request.trainee.CreateTraineeRequest;
import com.epam.gymcrm.request.trainee.UpdateTraineeRequest;


import java.util.List;


public interface TraineeMapper {

    TraineeDto toTraineeDto(Trainee trainee);

    List<TraineeDto> toTraineeDtoList(List<Trainee> trainees);

    Trainee createTrainee(CreateTraineeRequest request);

    void updateTraineeRequest(UpdateTraineeRequest request,  Trainee trainee);
}

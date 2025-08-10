package com.epam.gymcrm.request.trainee;

 import lombok.AllArgsConstructor;
 import lombok.Builder;
 import lombok.Data;
 import lombok.NoArgsConstructor;
 import java.util.List;

 @Data
 @AllArgsConstructor
 @NoArgsConstructor
 @Builder
public class UpdateTraineeTrainerListRequest {

     private List<Long> trainerIds;
}

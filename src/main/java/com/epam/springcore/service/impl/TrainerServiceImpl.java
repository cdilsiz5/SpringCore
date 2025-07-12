package com.epam.springcore.service.impl;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.request.CreateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.springcore.mapper.TrainerMapper.TRAINER_MAPPER;


@Service
public class TrainerServiceImpl implements ITrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerDao trainerDao;

    public TrainerServiceImpl(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public TrainerDto createTrainer(CreateTrainerRequest request) {
        log.info("Creating new trainer: {} {}", request.getFirstName(), request.getLastName());
        Trainer trainer = TRAINER_MAPPER.toTrainer(request);
        Trainer saved = trainerDao.save(trainer);
        log.debug("Trainer saved with userId={}", saved.getUserId());
        return TRAINER_MAPPER.toTrainerDto(saved);
    }

    @Override
    public TrainerDto getTrainer(String id) {
        log.info("Fetching trainer with ID: {}", id);
        Trainer trainer = trainerDao.findById(id);
        if (trainer == null) {
            log.warn("Trainer with ID {} not found", id);
            return null;
        }
        return TRAINER_MAPPER.toTrainerDto(trainer);
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        log.info("Fetching all trainers");
        return TRAINER_MAPPER.toTrainerDtoList((List<Trainer>) trainerDao.findAll());
    }

    @Override
    public TrainerDto updateTrainer(String id, CreateTrainerRequest request) {
        log.info("Updating trainer with ID: {}", id);
        Trainer existingTrainer = checkTrainerExist(id);
        TRAINER_MAPPER.updateTrainer(request, existingTrainer);
        Trainer updated = trainerDao.save(existingTrainer);
        log.debug("Trainer with ID {} updated", id);
        return TRAINER_MAPPER.toTrainerDto(updated);
    }

    @Override
    public void deleteTrainer(String id) {
        log.info("Deleting trainer with ID: {}", id);
        checkTrainerExist(id);
        trainerDao.delete(id);
        log.debug("Trainer with ID {} deleted", id);
    }

    private Trainer checkTrainerExist(String id) {
        Trainer existingTrainer = trainerDao.findById(id);
        if (existingTrainer == null) {
            log.error("Trainer with ID {} not found", id);
            throw new RuntimeException("Trainer with ID: " + id + " not found");
        }
        return existingTrainer;
    }
}

package at.jku.dke.task_app.dfm.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.dfm.data.entities.DfmTask;
import at.jku.dke.task_app.dfm.data.entities.DfmTaskGroup;
import at.jku.dke.task_app.dfm.data.entities.DfmEvaluationCriterion;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskGroupRepository;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskDto;
import at.jku.dke.task_app.dfm.parser.DfmParser;
import at.jku.dke.task_app.dfm.parser.DfmParseException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * This class provides methods for managing {@link DfmTask}s.
 */
@Service
public class
DfmTaskService extends BaseTaskInGroupService<DfmTask, DfmTaskGroup, ModifyDfmTaskDto> {

    private final MessageSource messageSource;
    private final DfmParser parser = new DfmParser();

    /**
     * Creates a new instance of class {@link DfmTaskService}.
     *
     * @param repository          The task repository.
     * @param taskGroupRepository The task group repository.
     * @param messageSource       The message source.
     */
    public DfmTaskService(DfmTaskRepository repository, DfmTaskGroupRepository taskGroupRepository, MessageSource messageSource) {
        super(repository, taskGroupRepository);
        this.messageSource = messageSource;
    }

    @Override
    protected DfmTask createTask(long id, ModifyTaskDto<ModifyDfmTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("dfm"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        validateTaskData(modifyTaskDto);
        var task = new DfmTask(modifyTaskDto.additionalData().solution());
        task.replaceEvaluationCriteria(toEntities(modifyTaskDto.additionalData().evaluationCriteria()));
        return task;
    }

    @Override
    protected void updateTask(DfmTask task, ModifyTaskDto<ModifyDfmTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("dfm"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        validateTaskData(modifyTaskDto);
        task.setSolution(modifyTaskDto.additionalData().solution());
        task.replaceEvaluationCriteria(toEntities(modifyTaskDto.additionalData().evaluationCriteria()));
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(DfmTask task, boolean create) {
        return new TaskModificationResponseDto(
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.ENGLISH)
        );
    }

    private void validateSolution(String solution) {
        try {
            parser.parse(solution);
        } catch (DfmParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid DFM solution.", ex);
        }
    }

    private void validateTaskData(ModifyTaskDto<ModifyDfmTaskDto> dto) {
            validateSolution(dto.additionalData().solution());
            var sum = dto.additionalData().evaluationCriteria().stream()
                .map(criterion -> {
                    try {
                        parser.parse(criterion.subtree());
                    } catch (DfmParseException ex) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid DFM evaluation criterion '" + criterion.name() + "'.", ex);
                    }
                    return criterion.points();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!dto.additionalData().evaluationCriteria().isEmpty() && sum.compareTo(dto.maxPoints()) != 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Evaluation criterion points must equal the task maximum points.");
            }
    }

    private java.util.List<DfmEvaluationCriterion> toEntities(
            java.util.List<at.jku.dke.task_app.dfm.dto.DfmEvaluationCriterionDto> criteria) {
            return criteria.stream()
                .map(c -> new DfmEvaluationCriterion(c.name(), c.points(), c.subtree(),
                    c.partialSolution(), c.allowIncorrectFactClass()))
                .collect(Collectors.toList());
    }
}

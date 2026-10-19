package at.jku.dke.task_app.dfm.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskInGroupService;
import at.jku.dke.task_app.dfm.data.entities.DfmTask;
import at.jku.dke.task_app.dfm.data.entities.DfmTaskGroup;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskGroupRepository;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/**
 * This class provides methods for managing {@link DfmTask}s.
 */
@Service
public class DfmTaskService extends BaseTaskInGroupService<DfmTask, DfmTaskGroup, ModifyDfmTaskDto> {

    private final MessageSource messageSource;

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
        return new DfmTask(modifyTaskDto.additionalData().solution());
    }

    @Override
    protected void updateTask(DfmTask task, ModifyTaskDto<ModifyDfmTaskDto> modifyTaskDto) {
        if (!modifyTaskDto.taskType().equals("dfm"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task type.");
        task.setSolution(modifyTaskDto.additionalData().solution());
    }

    @Override
    protected TaskModificationResponseDto mapToReturnData(DfmTask task, boolean create) {
        return new TaskModificationResponseDto(
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskDescription", null, Locale.ENGLISH)
        );
    }
}

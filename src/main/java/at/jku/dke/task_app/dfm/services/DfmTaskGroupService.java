package at.jku.dke.task_app.dfm.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskGroupModificationResponseDto;
import at.jku.dke.etutor.task_app.services.BaseTaskGroupService;
import at.jku.dke.task_app.dfm.data.entities.DfmTaskGroup;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskGroupRepository;
import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskGroupDto;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

/**
 * This class provides methods for managing {@link DfmTaskGroup}s.
 */
@Service
public class DfmTaskGroupService extends BaseTaskGroupService<DfmTaskGroup, ModifyDfmTaskGroupDto> {

    private final MessageSource messageSource;

    /**
     * Creates a new instance of class {@link DfmTaskGroupService}.
     *
     * @param repository    The task group repository.
     * @param messageSource The message source.
     */
    public DfmTaskGroupService(DfmTaskGroupRepository repository, MessageSource messageSource) {
        super(repository);
        this.messageSource = messageSource;
    }

    @Override
    protected DfmTaskGroup createTaskGroup(long id, ModifyTaskGroupDto<ModifyDfmTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("dfm"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");
        return new DfmTaskGroup(modifyTaskGroupDto.additionalData().minNumber(), modifyTaskGroupDto.additionalData().maxNumber());
    }

    @Override
    protected void updateTaskGroup(DfmTaskGroup taskGroup, ModifyTaskGroupDto<ModifyDfmTaskGroupDto> modifyTaskGroupDto) {
        if (!modifyTaskGroupDto.taskGroupType().equals("dfm"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task group type.");
        taskGroup.setMinNumber(modifyTaskGroupDto.additionalData().minNumber());
        taskGroup.setMaxNumber(modifyTaskGroupDto.additionalData().maxNumber());
    }

    @Override
    protected TaskGroupModificationResponseDto mapToReturnData(DfmTaskGroup taskGroup, boolean create) {
        return new TaskGroupModificationResponseDto(
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{taskGroup.getMinNumber(), taskGroup.getMaxNumber()}, Locale.GERMAN),
            this.messageSource.getMessage("defaultTaskGroupDescription", new Object[]{taskGroup.getMinNumber(), taskGroup.getMaxNumber()}, Locale.ENGLISH));
    }
}

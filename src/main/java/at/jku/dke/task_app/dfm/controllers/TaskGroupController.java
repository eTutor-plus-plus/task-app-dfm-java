package at.jku.dke.task_app.dfm.controllers;

import at.jku.dke.etutor.task_app.auth.AuthConstants;
import at.jku.dke.etutor.task_app.controllers.BaseTaskGroupController;
import at.jku.dke.task_app.dfm.data.entities.DfmTaskGroup;
import at.jku.dke.task_app.dfm.dto.MinMaxDto;
import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskGroupDto;
import at.jku.dke.task_app.dfm.dto.DfmTaskGroupDto;
import at.jku.dke.task_app.dfm.services.DfmTaskGroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Controller for managing {@link DfmTaskGroup}s.
 */
@RestController
public class TaskGroupController extends BaseTaskGroupController<DfmTaskGroup, DfmTaskGroupDto, ModifyDfmTaskGroupDto> {

    /**
     * Creates a new instance of class {@link TaskGroupController}.
     *
     * @param taskGroupService The task group service.
     */
    public TaskGroupController(DfmTaskGroupService taskGroupService) {
        super(taskGroupService);
    }

    @Override
    protected DfmTaskGroupDto mapToDto(DfmTaskGroup taskGroup) {
        return new DfmTaskGroupDto(taskGroup.getMinNumber(), taskGroup.getMaxNumber());
    }

    /**
     * Returns two random numbers.
     * <p>
     * This method is used to demonstrate how additional endpoints can be used.
     *
     * @return Two random numbers.
     */
    @GetMapping(value = "/random", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize(AuthConstants.CRUD_AUTHORITY)
    @SecurityRequirement(name = AuthConstants.API_KEY_REQUIREMENT)
    public ResponseEntity<MinMaxDto> getRandomNumbers() {
        var rand = new Random();
        var min = rand.nextInt(100);
        return ResponseEntity.ok(new MinMaxDto(min,  rand.nextInt(min + 1, 1000)));
    }

}

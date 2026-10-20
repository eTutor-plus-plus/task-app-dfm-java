package at.jku.dke.task_app.dfm.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseTaskController;
import at.jku.dke.task_app.dfm.data.entities.DfmTask;
import at.jku.dke.task_app.dfm.dto.DfmTaskDto;
import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskDto;
import at.jku.dke.task_app.dfm.services.DfmTaskService;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

/**
 * Controller for managing {@link DfmTask}s.
 */
@RestController
public class TaskController extends BaseTaskController<DfmTask, DfmTaskDto, ModifyDfmTaskDto> {

    /**
     * Creates a new instance of class {@link TaskController}.
     *
     * @param taskService The task service.
     */
    public TaskController(DfmTaskService taskService) {
        super(taskService);
    }

    @Override
    protected DfmTaskDto mapToDto(DfmTask task) {
        var criteria = task.getEvaluationCriteria().stream()
            .map(c -> new at.jku.dke.task_app.dfm.dto.DfmEvaluationCriterionDto(
                c.getName(), c.getPoints(), c.getSubtree(), c.isPartialSolution(), c.isAllowIncorrectFactClass()))
            .collect(Collectors.toList());
        return new DfmTaskDto(task.getSolution(), criteria);
    }

}

package at.jku.dke.task_app.dfm.controllers;

import at.jku.dke.etutor.task_app.controllers.BaseSubmissionController;
import at.jku.dke.task_app.dfm.data.entities.DfmSubmission;
import at.jku.dke.task_app.dfm.dto.DfmSubmissionDto;
import at.jku.dke.task_app.dfm.services.DfmSubmissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing {@link DfmSubmission}s.
 */
@RestController
public class SubmissionController extends BaseSubmissionController<DfmSubmissionDto> {
    /**
     * Creates a new instance of class {@link SubmissionController}.
     *
     * @param submissionService The input service.
     */
    public SubmissionController(DfmSubmissionService submissionService) {
        super(submissionService);
    }
}

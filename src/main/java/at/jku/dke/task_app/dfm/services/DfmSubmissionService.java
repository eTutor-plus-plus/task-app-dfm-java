package at.jku.dke.task_app.dfm.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.etutor.task_app.services.BaseSubmissionService;
import at.jku.dke.task_app.dfm.data.entities.DfmSubmission;
import at.jku.dke.task_app.dfm.data.entities.DfmTask;
import at.jku.dke.task_app.dfm.data.repositories.DfmSubmissionRepository;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import at.jku.dke.task_app.dfm.dto.DfmSubmissionDto;
import at.jku.dke.task_app.dfm.evaluation.EvaluationService;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for managing {@link DfmSubmission}s.
 */
@Service
public class DfmSubmissionService extends BaseSubmissionService<DfmTask, DfmSubmission, DfmSubmissionDto> {

    private final EvaluationService evaluationService;

    /**
     * Creates a new instance of class {@link DfmSubmissionService}.
     *
     * @param submissionRepository The input repository.
     * @param taskRepository       The task repository.
     * @param evaluationService    The evaluation service.
     */
    public DfmSubmissionService(DfmSubmissionRepository submissionRepository, DfmTaskRepository taskRepository, EvaluationService evaluationService) {
        super(submissionRepository, taskRepository);
        this.evaluationService = evaluationService;
    }

    @Override
    protected DfmSubmission createSubmissionEntity(SubmitSubmissionDto<DfmSubmissionDto> submitSubmissionDto) {
        return new DfmSubmission(submitSubmissionDto.submission().input());
    }

    @Override
    protected GradingDto evaluate(SubmitSubmissionDto<DfmSubmissionDto> submitSubmissionDto) {
        return this.evaluationService.evaluate(submitSubmissionDto);
    }

    @Override
    protected DfmSubmissionDto mapSubmissionToSubmissionData(DfmSubmission submission) {
        return new DfmSubmissionDto(submission.getSubmission());
    }

}

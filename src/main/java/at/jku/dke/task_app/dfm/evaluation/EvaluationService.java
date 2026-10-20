package at.jku.dke.task_app.dfm.evaluation;

import at.jku.dke.etutor.task_app.dto.CriterionDto;
import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import at.jku.dke.task_app.dfm.data.entities.DfmEvaluationCriterion;
import at.jku.dke.task_app.dfm.dto.DfmSubmissionDto;
import at.jku.dke.task_app.dfm.models.ast.AbstractElement;
import at.jku.dke.task_app.dfm.models.ast.DimensionElement;
import at.jku.dke.task_app.dfm.models.ast.FactElement;
import at.jku.dke.task_app.dfm.visualization.DfmVisualizationService;
import at.jku.dke.task_app.dfm.parser.DfmParseException;
import at.jku.dke.task_app.dfm.parser.DfmParser;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service that evaluates submissions.
 */
@Service
public class EvaluationService {
    private static final Logger LOG = LoggerFactory.getLogger(EvaluationService.class);

    private final DfmTaskRepository taskRepository;
    private final MessageSource messageSource;
    private final DfmParser parser;
    private final DfmVisualizationService visualizationService;

    /**
     * Creates a new instance of class {@link EvaluationService}.
     *
     * @param taskRepository The task repository.
     * @param messageSource  The message source.
     */
    public EvaluationService(DfmTaskRepository taskRepository, MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
        this.parser = new DfmParser();
        this.visualizationService = new DfmVisualizationService();
    }

    /**
     * Evaluates a input.
     *
     * @param submission The input to evaluate.
     * @return The evaluation result.
     */
    @Transactional
    public GradingDto evaluate(SubmitSubmissionDto<DfmSubmissionDto> submission) {
        var task = this.taskRepository.findById(submission.taskId()).orElseThrow(() -> new EntityNotFoundException("Task " + submission.taskId() + " does not exist."));
        LOG.info("Evaluating input for task {} with mode {} and feedback-level {}", submission.taskId(), submission.mode(), submission.feedbackLevel());
        return evaluateDfm(task.getSolution(), submission.submission().input(), task.getMaxPoints(),
            submission.mode(), submission.feedbackLevel(), Locale.of(submission.language()), task.getEvaluationCriteria());
    }

    /**
     * Evaluates a DFM submission against a textual DFM solution.
     *
     * @param solution    The expected DFM model.
     * @param input       The submitted DFM model.
     * @param maxPoints   The maximum number of points.
     * @param mode        The submission mode.
     * @param feedbackLevel The requested feedback level.
     * @param locale      The feedback locale.
     * @return The grading result.
     */
    public GradingDto evaluateDfm(String solution, String input, BigDecimal maxPoints,
                                  SubmissionMode mode, int feedbackLevel, Locale locale) {
        return evaluateDfm(solution, input, maxPoints, mode, feedbackLevel, locale, List.of());
    }

    public GradingDto evaluateDfm(String solution, String input, BigDecimal maxPoints,
                                  SubmissionMode mode, int feedbackLevel, Locale locale,
                                  List<DfmEvaluationCriterion> evaluationCriteria) {
        List<CriterionDto> criteria = new ArrayList<>();
        List<AbstractElement> expected = null;
        List<AbstractElement> actual = null;
        DfmParseException parseError = null;
        try {
            expected = parser.parse(solution);
            actual = parser.parse(input);
        } catch (DfmParseException ex) {
            parseError = ex;
        }

        boolean valid = parseError == null;
        criteria.add(new CriterionDto(
            messageSource.getMessage("criterium.syntax", null, locale),
            null,
            valid,
            valid ? messageSource.getMessage("criterium.syntax.valid.dfm", null, locale) : parseError.getMessage()));

        boolean correct = valid && modelsEqual(expected, actual);
        BigDecimal points = BigDecimal.ZERO;
        if (valid && !evaluationCriteria.isEmpty()) {
            criteria.remove(0);
            points = evaluateCriteria(evaluationCriteria, actual, criteria, feedbackLevel, locale);
            correct = points.compareTo(maxPoints) == 0;
        }
        if (valid && feedbackLevel > 0) {
            criteria.add(new CriterionDto(
                messageSource.getMessage("criterium.visualization", null, locale), null, true,
                visualizationService.render(actual)));
        }
        String feedback;
        switch (mode) {
            case RUN -> feedback = messageSource.getMessage("input", new Object[]{input}, locale);
            case DIAGNOSE -> {
                feedback = messageSource.getMessage(correct ? "correct" : "incorrect", null, locale);
                if (correct) {
                    points = maxPoints;
                }
                if (feedbackLevel > 0 && valid && evaluationCriteria.isEmpty()) {
                    criteria.add(new CriterionDto(
                        messageSource.getMessage("criterium.value", null, locale),
                        correct ? maxPoints : BigDecimal.ZERO,
                        correct,
                        messageSource.getMessage(correct ? "criterium.value.equals.dfm" : "criterium.value.different.dfm", null, locale)));
                }
            }
            case SUBMIT -> {
                feedback = messageSource.getMessage(correct ? "correct" : "incorrect", null, locale);
                if (correct) {
                    points = maxPoints;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
        return new GradingDto(maxPoints, points, feedback, criteria);
    }

    private BigDecimal evaluateCriteria(List<DfmEvaluationCriterion> evaluationCriteria,
                                        List<AbstractElement> actual, List<CriterionDto> result,
                                        int feedbackLevel, Locale locale) {
        BigDecimal points = BigDecimal.ZERO;
        for (var criterion : evaluationCriteria) {
            boolean passed = false;
            String feedback;
            try {
                var expected = parser.parse(criterion.getSubtree());
                passed = expected.stream().allMatch(element ->
                    findElement(element.getName(), actual)
                        .map(candidate -> matches(candidate, element, criterion.isPartialSolution(),
                            criterion.isAllowIncorrectFactClass()))
                        .orElse(false));
                feedback = messageSource.getMessage(
                    passed ? "criterium.evaluation.passed" : "criterium.evaluation.notMet", null, locale);
            } catch (DfmParseException ex) {
                feedback = ex.getMessage();
            }
            if (passed) {
                points = points.add(criterion.getPoints());
            }
            if (feedbackLevel > 0) {
                result.add(new CriterionDto(criterion.getName(),
                    passed ? criterion.getPoints() : BigDecimal.ZERO, passed, feedback));
            }
        }
        return points;
    }

    private boolean matches(AbstractElement actual, AbstractElement expected,
                            boolean partialSolution, boolean allowIncorrectFactClass) {
        if (actual instanceof FactElement actualFact && expected instanceof FactElement expectedFact) {
            if (allowIncorrectFactClass && actualFact.getName().equals(expectedFact.getName())) {
                return true;
            }
            if (!expectedFact.structurallyEqualsWithoutDimensions(actualFact)) {
                return false;
            }
            if (partialSolution && expectedFact.getDimensions().isEmpty()) {
                return true;
            }
            return expectedFact.getDimensions().stream().allMatch(expectedDimension ->
                actualFact.getDimensions().stream().anyMatch(actualDimension ->
                    matches(actualDimension, expectedDimension, partialSolution, allowIncorrectFactClass)));
        }
        if (actual instanceof DimensionElement actualDimension && expected instanceof DimensionElement expectedDimension) {
            return partialSolution
                ? actualDimension.containsAllHierarchies(expectedDimension)
                : actualDimension.structurallyEquals(expectedDimension);
        }
        return !partialSolution && expected.structurallyEquals(actual);
    }

    private java.util.Optional<AbstractElement> findElement(String name, List<AbstractElement> elements) {
        for (var element : elements) {
            if (element.getName().equals(name)) {
                return java.util.Optional.of(element);
            }
            if (element instanceof FactElement fact) {
                var nested = findElement(name, new ArrayList<>(fact.getDimensions()));
                if (nested.isPresent()) {
                    return nested;
                }
            }
        }
        return java.util.Optional.empty();
    }

    private boolean modelsEqual(List<AbstractElement> expected, List<AbstractElement> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).structurallyEquals(actual.get(i))) {
                return false;
            }
        }
        return true;
    }
}

package at.jku.dke.task_app.dfm.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.dfm.data.entities.DfmEvaluationCriterion;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DfmEvaluationServiceTest {
    private final EvaluationService service = new EvaluationService(
        (DfmTaskRepository) null,
        messages()
    );

    @Test
    void awardsPointsForStructurallyEqualDfmModels() {
        var result = service.evaluateDfm(
            "fact Sales { profit; }; dimension Product { product - category; }; Sales - Product;",
            "fact Sales { profit; }; dimension Product { product - category; }; Sales - Product;",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 3, Locale.ENGLISH);

        assertEquals(BigDecimal.TEN, result.points());
        assertEquals("Your solution is correct.", result.generalFeedback());
        assertTrue(result.criteria().get(0).passed());
    }

    @Test
    void reportsInvalidDfmSyntaxWithoutAwardingPoints() {
        var result = service.evaluateDfm(
            "fact Sales { profit; };",
            "fact Sales { ;",
            BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3, Locale.ENGLISH);

        assertEquals(BigDecimal.ZERO, result.points());
        assertEquals("Your solution is incorrect.", result.generalFeedback());
        assertTrue(!result.criteria().get(0).passed());
    }

    @Test
    void awardsIndividualCriterionPoints() {
        var criteria = java.util.List.of(
            new DfmEvaluationCriterion("Profit", BigDecimal.valueOf(4),
                "fact Sales { profit; };", false, false),
            new DfmEvaluationCriterion("Product", BigDecimal.valueOf(6),
                "dimension Product { product - category; };", true, false)
        );

        var result = service.evaluateDfm(
            "fact Sales { profit; }; dimension Product { product - category; }; Sales - Product;",
            "fact Sales { profit; }; dimension Product { product - category; }; Sales - Product;",
            BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3, Locale.ENGLISH, criteria);

        assertEquals(BigDecimal.TEN, result.points());
        assertEquals(3, result.criteria().size());
        assertTrue(result.criteria().stream().allMatch(c -> c.passed()));
    }

    private static MessageSource messages() {
        var source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("classpath:messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        return source;
    }
}

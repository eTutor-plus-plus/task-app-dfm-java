package at.jku.dke.task_app.dfm.evaluation;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.task_app.dfm.data.entities.DfmEvaluationCriterion;
import at.jku.dke.task_app.dfm.data.repositories.DfmTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
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
    void ignoresCommentsWhenComparingDfmModels() {
        var result = service.evaluateDfm(
            "// solution\nfact Sales { profit; };",
            "fact Sales { profit; }; // my answer\n",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 3, Locale.ENGLISH);

        assertEquals(BigDecimal.TEN, result.points());
        assertTrue(result.criteria().get(0).passed());
    }

    @Test
    void connectionDescriptionsAreRelevantForGrading() {
        var solution = "fact S { m; }; dimension C { customer -[customercity]- city; }; S - C;";

        var same = service.evaluateDfm(solution, solution, BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);
        var otherLabel = service.evaluateDfm(solution,
            "fact S { m; }; dimension C { customer -[livesin]- city; }; S - C;",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);
        var noLabel = service.evaluateDfm(solution,
            "fact S { m; }; dimension C { customer - city; }; S - C;",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);

        assertEquals(BigDecimal.TEN, same.points());
        assertEquals(BigDecimal.ZERO, otherLabel.points());
        assertEquals(BigDecimal.ZERO, noLabel.points());
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

    @ParameterizedTest
    @EnumSource(value = SubmissionMode.class, names = {"RUN", "DIAGNOSE"})
    void includesSvgVisualizationForRunAndDiagnose(SubmissionMode mode) {
        var source = "fact Sales { profit; }; dimension Product { product - category; }; Sales - Product;";

        var result = service.evaluateDfm(source, source, BigDecimal.TEN, mode, 1, Locale.ENGLISH);

        var visualizations = result.criteria().stream()
            .filter(criterion -> criterion.feedback().startsWith("<svg "))
            .toList();
        assertEquals(1, visualizations.size());
        assertTrue(visualizations.get(0).feedback().contains("</svg>"));
        assertTrue(visualizations.get(0).passed());
    }

    @Test
    void omitsSvgVisualizationWhenFeedbackIsDisabled() {
        var source = "fact Sales { profit; };";

        var result = service.evaluateDfm(source, source, BigDecimal.TEN,
            SubmissionMode.RUN, 0, Locale.ENGLISH);

        assertTrue(result.criteria().stream().noneMatch(criterion -> criterion.feedback().startsWith("<svg ")));
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

    @Test
    void factDimensionConnectionDescriptionsAreRelevantForGrading() {
        var solution = "fact S { m; }; dimension T { d; }; S -[soldat]- T;";

        var same = service.evaluateDfm(solution, solution, BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);
        var otherLabel = service.evaluateDfm(solution, "fact S { m; }; dimension T { d; }; S -[x]- T;",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);
        var noLabel = service.evaluateDfm(solution, "fact S { m; }; dimension T { d; }; S - T;",
            BigDecimal.TEN, SubmissionMode.SUBMIT, 0, Locale.ENGLISH);

        assertEquals(BigDecimal.TEN, same.points());
        assertEquals(BigDecimal.ZERO, otherLabel.points());
        assertEquals(BigDecimal.ZERO, noLabel.points());
    }

    @Test
    void dimensionCriterionIgnoresFactConnectionDescription() {
        var criteria = java.util.List.of(
            new DfmEvaluationCriterion("Dimension", BigDecimal.TEN, "dimension T { d; };", false, false),
            new DfmEvaluationCriterion("Connection", BigDecimal.TEN,
                "fact S { m; }; dimension T { d; }; S -[soldat]- T;", false, false));

        var withLabel = service.evaluateDfm("fact S { m; }; dimension T { d; }; S -[soldat]- T;",
            "fact S { m; }; dimension T { d; }; S -[soldat]- T;",
            BigDecimal.valueOf(20), SubmissionMode.DIAGNOSE, 3, Locale.ENGLISH, criteria);
        var withoutLabel = service.evaluateDfm("fact S { m; }; dimension T { d; }; S -[soldat]- T;",
            "fact S { m; }; dimension T { d; }; S - T;",
            BigDecimal.valueOf(20), SubmissionMode.DIAGNOSE, 3, Locale.ENGLISH, criteria);

        assertEquals(BigDecimal.valueOf(20), withLabel.points());
        assertEquals(BigDecimal.TEN, withoutLabel.points());
    }

    private static final String SHARED_MODEL = """
        fact Sales { profit; };
        hierarchy VAT { VAT - vatClass; };
        hierarchy city { city = region; city - country - {cross} VAT; };
        dimension city { {shared} city; };
        dimension customer { customer -[customerCity]- {shared} city; };
        dimension product { product - category - {cross} VAT; };
        Sales -[storeCity]- city;
        Sales - customer;
        Sales - product;
        """;

    @Test
    void hierarchyDeclarationOrderIsIrrelevantForGrading() {
        var reordered = """
            fact Sales { profit; };
            hierarchy city { city - country - {cross} VAT; city = region; };
            hierarchy VAT { VAT - vatClass; };
            dimension city { {shared} city; };
            dimension customer { customer -[customerCity]- {shared} city; };
            dimension product { product - category - {cross} VAT; };
            Sales -[storeCity]- city;
            Sales - customer;
            Sales - product;
            """;

        var result = service.evaluateDfm(SHARED_MODEL, reordered, BigDecimal.TEN, SubmissionMode.SUBMIT, 0,
            Locale.ENGLISH);

        assertEquals(BigDecimal.TEN, result.points());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "hierarchy city { city - region; city - country - {cross} VAT; };",
        "hierarchy city { city = region; city - country; };",
        "hierarchy city { city = region; city - country - {shared} VAT; };",
        "hierarchy city { city = region; city - country - {cross} VAT; city - zip; };"
    })
    void differentHierarchyDeclarationsAreNotEqual(String city) {
        var input = SHARED_MODEL.replace("hierarchy city { city = region; city - country - {cross} VAT; };", city);

        var result = service.evaluateDfm(SHARED_MODEL, input, BigDecimal.TEN, SubmissionMode.SUBMIT, 0,
            Locale.ENGLISH);

        assertEquals(BigDecimal.ZERO, result.points());
    }

    @Test
    void missingHierarchyDeclarationIsReportedAsSyntaxError() {
        var input = SHARED_MODEL.replace("hierarchy VAT { VAT - vatClass; };", "");

        var result = service.evaluateDfm(SHARED_MODEL, input, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH);

        assertEquals(BigDecimal.ZERO, result.points());
        assertTrue(!result.criteria().get(0).passed());
        assertTrue(result.criteria().get(0).feedback().contains("Hierarchy VAT is not declared"));
    }

    @Test
    void awardsCriterionPointsForSharedHierarchies() {
        var criteria = java.util.List.of(
            new DfmEvaluationCriterion("Customer", BigDecimal.valueOf(3),
                "hierarchy city { city; }; dimension customer { customer -[customerCity]- {shared} city; };",
                false, false),
            new DfmEvaluationCriterion("City", BigDecimal.valueOf(4),
                "hierarchy VAT { VAT; }; hierarchy city { city = region; city - country - {cross} VAT; };",
                false, false),
            new DfmEvaluationCriterion("VAT", BigDecimal.valueOf(3),
                "hierarchy VAT { VAT - vatClass; };", true, false));

        var result = service.evaluateDfm(SHARED_MODEL, SHARED_MODEL, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH, criteria);

        assertEquals(BigDecimal.TEN, result.points());
        assertTrue(result.criteria().stream().allMatch(c -> c.passed()));
    }

    @Test
    void hierarchyCriterionIsNotConfusedWithDimensionOfTheSameName() {
        var criteria = java.util.List.of(
            new DfmEvaluationCriterion("City", BigDecimal.TEN,
                "hierarchy city { city = region; city - country - {cross} VAT; }; hierarchy VAT { VAT; };",
                false, false));
        var otherHierarchy = SHARED_MODEL.replace("city = region;", "city - region;");

        var passed = service.evaluateDfm(SHARED_MODEL, SHARED_MODEL, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH, criteria);
        var failed = service.evaluateDfm(SHARED_MODEL, otherHierarchy, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH, criteria);

        assertEquals(BigDecimal.TEN, passed.points());
        assertEquals(BigDecimal.ZERO, failed.points());
    }

    @Test
    void partialHierarchyCriterionAcceptsAdditionalChains() {
        var criteria = java.util.List.of(
            new DfmEvaluationCriterion("City", BigDecimal.TEN, "hierarchy city { city = region; };", true, false));
        var withExtraChain = SHARED_MODEL.replace("city = region;", "city = region; city - zip;");
        var withoutRegion = SHARED_MODEL.replace("city = region;", "city - zip;");

        var accepted = service.evaluateDfm(SHARED_MODEL, withExtraChain, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH, criteria);
        var rejected = service.evaluateDfm(SHARED_MODEL, withoutRegion, BigDecimal.TEN, SubmissionMode.DIAGNOSE, 3,
            Locale.ENGLISH, criteria);

        assertEquals(BigDecimal.TEN, accepted.points());
        assertEquals(BigDecimal.ZERO, rejected.points());
    }

    private static MessageSource messages() {
        var source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("classpath:messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        return source;
    }
}

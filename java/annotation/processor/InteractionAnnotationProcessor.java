package com.cfa.sts.integration.common.annotation.processor;

import com.cfa.sts.integration.common.annotation.Interaction;
import com.cfa.sts.integration.common.enums.DirectionEnum;
import com.cfa.sts.integration.common.enums.FunctionEnum;
import com.cfa.sts.integration.common.enums.TransportTypeEnum;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@SupportedAnnotationTypes(
        "com.cfa.sts.integration.common.annotation.Interaction"
)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class InteractionAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> contextSet = new HashSet<>();
    private static final String validContextRegex = "^[a-zA-Z0-9]+$";
    private static final Pattern validContextPattern = Pattern.compile(validContextRegex);
    private static final Set<TransportTypeEnum> supportedDirection_In_Transport = Set.of(
            TransportTypeEnum.HTTP, TransportTypeEnum.SQS);
    private static final Set<TransportTypeEnum> supportedDirection_Out_Transport = Set.of(
            TransportTypeEnum.EMAIL, TransportTypeEnum.SMTP,
            TransportTypeEnum.HTTP, TransportTypeEnum.SQS);
    private static final BiPredicate<DirectionEnum, TransportTypeEnum> directionAgreesWithTransport = (direction, transport) ->
            switch(direction) {
                case IN -> supportedDirection_In_Transport.contains(transport);
                case OUT -> supportedDirection_Out_Transport.contains(transport);
                case INTERNAL -> transport.equals(TransportTypeEnum.NA);
            };
    private static final BiPredicate<DirectionEnum, FunctionEnum> directionAgreesWithFunction = (direction, function) ->
            switch(direction) {
                case INTERNAL -> function.equals(FunctionEnum.NA);
                default -> true;
            };
    private static final Predicate<String> contextHasValidFormat = c -> validContextPattern.matcher(c).matches();
    private static final Predicate<String> contextIsUnique = c -> !contextSet.contains(c);

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);

            annotatedElements.forEach(element -> {
                Interaction interaction = element.getAnnotation(Interaction.class);
                checkForRuleViolations(interaction, element);
                contextSet.add(interaction.context());
            });

        }

        return false;
    }

    private void checkForRuleViolations(Interaction interaction, Element element) {
        if (!directionAgreesWithTransport.test(interaction.direction(), interaction.transport())) {
            failTheCompilation(
                    String.format("Direction <%s> and Transport <%s> don't agree",
                            interaction.direction().getValue(), interaction.transport().getValue()),
                    element);
        }

        if (!directionAgreesWithFunction.test(interaction.direction(), interaction.function())) {
            failTheCompilation(
                    String.format("Direction <%s> and Function <%s> don't agree",
                            interaction.direction().getValue(), interaction.function().getValue()),
                    element);
        }

        if (!contextHasValidFormat.test(interaction.context())) {
            failTheCompilation(
                    String.format("Context <%s> has invalid characters", interaction.context()), element
            );
        }

        if (!contextIsUnique.test(interaction.context())) {
            failTheCompilation(
                    String.format("Context <%s> is duplicated", interaction.context()), element
            );
        }
    }
    private void failTheCompilation(String message, Element element) {
        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, "Interaction " + message, element);
    }

}
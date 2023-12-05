package com.epam.rd.autocode.assessment.basics.collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.factory.Factory;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Java8UsingTest {

    private static final Factory spoon;

    static {
        final String[] args = {"-i", "src/main/java/"};
        final Launcher launcher = new Launcher();
        launcher.setArgs(args);
        launcher.buildModel();
        spoon = launcher.getFactory();
    }

    @Test
    @DisplayName("Methods are using Streams and Lambdas")
    void testComplianceFindSortAndAddMethodsInStoreShouldUseForbiddenAPI() {
        CtType<Store> agencyClass = spoon.Type().get(Store.class.getName());
        Class<?>[] classes = {Find.class, Sort.class};
        Arrays.stream(classes)
                .map(Class::getDeclaredMethods)
                .flatMap(Stream::of)
                .flatMap(m -> agencyClass.getMethodsByName(m.getName()).stream())
                .forEach(m -> assertTrue(
                        m.getReferencedTypes().stream()
                                .map(CtTypeInformation::getQualifiedName)
                                .filter(name -> name.startsWith("java.util.stream")
                                        || name.startsWith("java.util.function"))
                                .map(el -> Boolean.TRUE)
                                .findAny().orElse(Boolean.FALSE),
                        () -> "Method " + m.getSignature() + " must use Stream API and types from the "
                                + "java.util.function package")
                );
    }
}

package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebConfigTest {

    private final WebApplicationContextRunner contextRunner =
            new WebApplicationContextRunner().withUserConfiguration(WebConfig.class);

    @Test
    void shouldContainInternalResourceViewResolver() {
        contextRunner.run(context -> {
            ViewResolver resolver = context.getBean(ViewResolver.class);
            assertThat(resolver).isInstanceOf(ViewResolverComposite.class);

            ViewResolverComposite composite = (ViewResolverComposite) resolver;
            List<ViewResolver> resolvers = getResolvers(composite);

            boolean hasInternal = resolvers.stream()
                    .anyMatch(r -> r instanceof InternalResourceViewResolver);

            assertThat(hasInternal).isTrue();
        });
    }

    // reflection to access private field "viewResolvers"
    private List<ViewResolver> getResolvers(ViewResolverComposite composite) {
        try {
            Field field = ViewResolverComposite.class.getDeclaredField("viewResolvers");
            field.setAccessible(true);
            return (List<ViewResolver>) field.get(composite);
        } catch (Exception e) {
            throw new RuntimeException("Could not extract viewResolvers", e);
        }
    }
}



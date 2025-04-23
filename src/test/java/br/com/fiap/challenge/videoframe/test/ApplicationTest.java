package br.com.fiap.challenge.videoframe.test;

import br.com.fiap.challenge.videoframe.Application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "AWS_REGION=us-east-1")
@ActiveProfiles("test")
class ApplicationTest {

    private static final String ARG = "";
    private static final String[] ARGS = new String[]{ARG};

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    void thenExecuteApplicationThenInitializeFine() {
        try (MockedStatic<Application> appStatic = Mockito.mockStatic(Application.class);
             MockedStatic<SpringApplication> springStatic = Mockito.mockStatic(SpringApplication.class)) {
            appStatic.when(() -> Application.main(ARGS))
                    .thenCallRealMethod();
            springStatic.when(() -> SpringApplication.run(Application.class, ARGS))
                    .thenReturn(context);

            // when
            Application.main(ARGS);
            Assertions.assertEquals("", ARG);
        }
    }
}

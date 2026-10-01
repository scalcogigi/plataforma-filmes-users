package org.example.exception;

import org.example.controller.UserController;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    @Test
    void deveRetornar500SemExporDetalhesInternos() throws Exception {
        UserService service = mock(UserService.class);
        when(service.findAll()).thenThrow(new IllegalStateException("detalhes privados do banco"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();

        mvc.perform(get("/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro interno no servidor."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}

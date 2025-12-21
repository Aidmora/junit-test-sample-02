package epn.edu.ec.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CakesResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.model.cake.UpdateCakeRequest;
import epn.edu.ec.service.CakeService;
import io.cucumber.java.bs.A;
import io.cucumber.plugin.event.Result;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = CakeController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@ActiveProfiles("test")
public class CakeControllerTest {
        @Autowired //
        private MockMvc mockMvc; // simula las llamadas a los servicios REST - Simula la llamada desde el
                                 // navegador hacia el controlador
        @MockitoBean
        private CakeService cakeService; // simula el servicio
        private final long cakeId = 1;
        private final CakeResponse mockCakeResponse = new CakeResponse(
                        cakeId, "Mock Cake", "Mock Cake Description");
        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void getCakes_ShouldReturnListOfCakes() throws Exception {
                // ARRANGE
                CakesResponse cakesResponse = new CakesResponse(List.of(mockCakeResponse));
                when(cakeService.getCakes()).thenReturn(cakesResponse);
                // ACT
                ResultActions result = mockMvc.perform(get("/cakes").contentType("application/json"));

                // ASSERT
                // Verificar el código de estado HTTP
                result.andExpect(status().isOk());
                result.andExpect(content().contentType("application/json"));
                result.andExpect(content().json(objectMapper.writeValueAsString(cakesResponse)));
                // Verificar el contenido de la respuesta
                System.out.println(result.andReturn().getResponse().getContentAsString());
                verify(cakeService, times(1)).getCakes();
        }

        @Test
        public void createCake_ShouldSaveAndReturnNewCake() throws Exception {
                // ARRANGE
                // Request
                CreateCakeRequest createCakeRequest = CreateCakeRequest.builder().title("New Cake")
                                .description("New Cake Description").build();
                CakeResponse createCakeResponse = CakeResponse.builder().id(2L).title("New Cake")
                                .description("New Cake Description").build();
                when(cakeService.createCake(createCakeRequest)).thenReturn(createCakeResponse);
                // ACT
                ResultActions result = mockMvc
                                .perform(post("/cakes").contentType("application/json")
                                                .content(objectMapper.writeValueAsBytes(createCakeRequest)));
                // ASSERT
                result.andExpect(status().isCreated());

        }

        // Test para el laboratorio 5
        @Test
        public void getCakes_shouldReturnEmptyList() throws Exception {
                // ARRANGE
                CakesResponse emptyResponse = new CakesResponse(List.of());
                when(cakeService.getCakes()).thenReturn(emptyResponse);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes")
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(emptyResponse)));

                verify(cakeService, times(1)).getCakes();
        }

        @Test
        public void getCakeById_shouldReturnCake() throws Exception {
                // ARRANGE
                when(cakeService.getCakeById(cakeId)).thenReturn(mockCakeResponse);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes/{id}", cakeId)
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(mockCakeResponse)));

                verify(cakeService, times(1)).getCakeById(cakeId);
        }

        @Test
        public void getCakeById_shouldReturnNotFound() throws Exception {
                // ARRANGE
                long nonExistentId = 99L;
                // Simulamos que el servicio lanza la excepción cuando no encuentra el ID
                when(cakeService.getCakeById(nonExistentId)).thenThrow(new CakeNotFoundException());

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes/{id}", nonExistentId)
                                .contentType("application/json"));

                // ASSERT
                // Se espera un 404 Not Found
                result.andExpect(status().isNotFound());

                verify(cakeService, times(1)).getCakeById(nonExistentId);
        }

        @Test
        public void updateCake_shouldUpdateCake() throws Exception {
                // ARRANGE
                UpdateCakeRequest updateRequest = UpdateCakeRequest.builder()
                                .title("Updated Title")
                                .description("Updated Description")
                                .build();

                CakeResponse updatedResponse = CakeResponse.builder()
                                .id(cakeId)
                                .title("Updated Title")
                                .description("Updated Description")
                                .build();
                when(cakeService.updateCake(cakeId, updateRequest))
                                .thenReturn(updatedResponse);

                // ACT
                ResultActions result = mockMvc.perform(put("/cakes/{id}", cakeId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updateRequest)));

                // ASSERT
                result.andExpect(status().isOk())
                                .andExpect(content().json(objectMapper.writeValueAsString(updatedResponse)));
                verify(cakeService, times(1)).updateCake(cakeId, updateRequest);
        }

        @Test
        public void updateCake_shouldReturnNotFound() throws Exception {
                // ARRANGE
                long nonExistentId = 99L;
                UpdateCakeRequest updateRequest = UpdateCakeRequest.builder()
                                .title("Title")
                                .description("Desc")
                                .build();
                when(cakeService.updateCake(nonExistentId, updateRequest))
                                .thenThrow(new CakeNotFoundException());
                // ACT
                ResultActions result = mockMvc.perform(put("/cakes/{id}", nonExistentId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updateRequest)));

                // ASSERT
                result.andExpect(status().isNotFound());
                verify(cakeService, times(1)).updateCake(nonExistentId, updateRequest);
        }

        @Test
        public void deleteCake_shouldDeleteCake() throws Exception {
                // ARRANGE
                // No es necesario configurar el mock para un método void si no lanza excepción

                // ACT
                ResultActions result = mockMvc.perform(delete("/cakes/{id}", cakeId)
                                .contentType("application/json"));

                // ASSERT
                // Generalmente un delete exitoso retorna 204 No Content
                result.andExpect(status().isNoContent());

                verify(cakeService, times(1)).deleteCake(cakeId);
        }
}

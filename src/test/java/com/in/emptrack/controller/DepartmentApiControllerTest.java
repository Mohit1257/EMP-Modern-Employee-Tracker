package com.in.emptrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.in.emptrack.controller.api.DepartmentApiController;
import com.in.emptrack.dto.request.DepartmentFormRequest;
import com.in.emptrack.dto.response.DepartmentResponse;
import com.in.emptrack.service.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("GET /api/departments should return list of departments")
    void testListDepartments() throws Exception {
        DepartmentResponse dept = DepartmentResponse.builder()
                .id(1L)
                .departmentCode("ENG")
                .name("Engineering")
                .build();

        when(departmentService.getAllDepartments()).thenReturn(List.of(dept));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].departmentCode").value("ENG"));
    }

    @Test
    @DisplayName("POST /api/departments should return 201 Created on valid input")
    void testCreateDepartment() throws Exception {
        DepartmentFormRequest req = DepartmentFormRequest.builder()
                .departmentCode("ENG")
                .name("Engineering")
                .build();

        DepartmentResponse resp = DepartmentResponse.builder()
                .id(1L)
                .departmentCode("ENG")
                .name("Engineering")
                .build();

        when(departmentService.createDepartment(any(DepartmentFormRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Engineering"));
    }
}

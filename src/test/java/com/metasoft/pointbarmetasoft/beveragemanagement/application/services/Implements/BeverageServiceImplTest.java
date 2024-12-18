package com.metasoft.pointbarmetasoft.beveragemanagement.application.services.Implements;

import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.requestDto.BeverageRequestDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.application.dtos.responseDto.BeverageResponseDto;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Category;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.BeverageRepository;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.CategoryRepository;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories.BusinessRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.shared.storage.FirebaseFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeverageServiceImplTest {

    @InjectMocks
    private BeverageServiceImpl beverageService;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FirebaseFileService firebaseFileService;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void createBeverage() throws IOException {
        // Arrange
        Long businessId = 1L;
        Long categoryId = 2L;

        BeverageRequestDto requestDto = new BeverageRequestDto();
        requestDto.setName("Cerveza Pilsen");
        requestDto.setDescription("Una unidad de cerveza");
        requestDto.setPrice(9.50);
        requestDto.setCategoryId(categoryId);

        MockMultipartFile mockImage = new MockMultipartFile("image", "image.png", "image/png", "imageData".getBytes());
        requestDto.setImage(mockImage);

        Business business = new Business();
        business.setId(businessId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Beer");

        Beverage beverage = new Beverage();
        beverage.setName("Cerveza Pilsen");
        beverage.setBusiness(business);
        beverage.setCategory(category);

        when(businessRepository.findById(businessId)).thenReturn(Optional.of(business));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(firebaseFileService.saveImage(any(MultipartFile.class))).thenReturn("imageUrl");
        when(modelMapper.map(requestDto, Beverage.class)).thenReturn(beverage);
        when(beverageRepository.save(any(Beverage.class))).thenReturn(beverage);
        when(modelMapper.map(beverage, BeverageResponseDto.class)).thenReturn(new BeverageResponseDto());

        // Act
        ApiResponse<?> response = beverageService.createBeverage(requestDto, businessId);

        // Assert
        assertTrue(response.getSuccess(), "Expected success response but got failure");
        assertEquals("Beverage created successfully", response.getMessage(), "The message returned was not as expected.");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void getAllBeverages() {
        // Arrange
        Long businessId = 1L;
        Beverage beverage1 = new Beverage();
        beverage1.setName("Cerveza Cristal");
        Category category1 = new Category();
        category1.setName("Beers");
        beverage1.setCategory(category1);

        Beverage beverage2 = new Beverage();
        beverage2.setName("Cerveza Cusqueña");
        Category category2 = new Category();
        category2.setName("Beers");
        beverage2.setCategory(category2);

        List<Beverage> beverages = new ArrayList<>();
        beverages.add(beverage1);
        beverages.add(beverage2);

        when(beverageRepository.findByBusinessId(businessId)).thenReturn(beverages);
        when(modelMapper.map(any(Beverage.class), eq(BeverageResponseDto.class)))
                .thenAnswer(invocation -> {
                    Beverage beverage = invocation.getArgument(0);
                    BeverageResponseDto dto = new BeverageResponseDto();
                    dto.setName(beverage.getName());
                    dto.setCategoryName(beverage.getCategory().getName());
                    return dto;
                });

        // Act
        List<BeverageResponseDto> result = beverageService.getAllBeverages(businessId);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cerveza Cristal", result.get(0).getName());
        assertEquals("Cerveza Cusqueña", result.get(1).getName());
        verify(beverageRepository, times(1)).findByBusinessId(businessId);
    }

    @Test
    void deleteBeverageSuccess() {
        // Arrange
        Long beverageId = 1L;
        Long businessId = 1L; // Este debe coincidir con el businessId de la bebida
        Beverage beverage = new Beverage();
        beverage.setId(beverageId);
        Business business = new Business();
        business.setId(businessId);
        beverage.setBusiness(business);

        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));

        // Act
        ApiResponse<?> response = beverageService.deleteBeverage(beverageId, businessId);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Beverage deleted successfully", response.getMessage());
        verify(beverageRepository, times(1)).delete(beverage);
    }

    @Test
    void deleteBeverageNotFound() {
        // Arrange
        Long beverageId = 1L;
        Long businessId = 1L;
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            beverageService.deleteBeverage(beverageId, businessId);
        });

        assertEquals("Beverage not found", exception.getMessage());
        verify(beverageRepository, never()).delete(any(Beverage.class));
    }
}
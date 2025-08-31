package com.eshoppingzone.userProfileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.payload.response.MessageResponse;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import com.eshoppingzone.userProfileService.service.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Users user;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("1234");
    }

    @Test
    public void testGetAllUsers() {
        List<Users> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<Users> result = adminService.getAllUsers();

        assertEquals(1, result.size());
        assertNull(result.get(0).getPassword());
        verify(userRepository).findAll();
    }

    @Test
    public void testGetUserById_Found() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Users result = adminService.getUserById(1);

        assertEquals("john", result.getUsername());
        assertNull(result.getPassword());
        verify(userRepository).findById(1);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adminService.getUserById(1);
        });

        assertEquals("User not found with id: 1", ex.getMessage());
    }

    @Test
    public void testDeleteUser_Success() {
        when(userRepository.existsById(1)).thenReturn(true);

        MessageResponse response = adminService.deleteUser(1);

        assertEquals("User deleted successfully", response.getMessage());
        verify(userRepository).deleteById(1);
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userRepository.existsById(1)).thenReturn(false);

        MessageResponse response = adminService.deleteUser(1);

        assertEquals("Error: User not found", response.getMessage());
        verify(userRepository, never()).deleteById(anyInt());
    }
}
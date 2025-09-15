package rnd.fahim.java21springboot3devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    private static final String PONG_PREFIX = "pong at";
    private static final String WELCOME_PREFIX = "Welcome to Spring Boot 3.5 at";
    private static final String IP_ADDRESS_TEXT = "IP address";
    private static final String FALLBACK_IP = "NONE";
    private static final String VALID_IP = "192.168.1.100";

    @Spy
    @InjectMocks
    private TestController testController;

    @BeforeEach
    void setUp() {
        reset(testController);
    }

    @Test
    void ping_ShouldReturnResponseWithPongMessage() {
        ResponseDto response = testController.ping();
        
        assertThat(response.message()).startsWith(PONG_PREFIX);
        assertThat(response).isNotNull();
    }

    @Test
    void welcome_WithValidIpAddress_ShouldReturnWelcomeMessageWithIp() {
        doReturn(VALID_IP).when(testController).getLocalIpAddress();
        
        ResponseDto response = testController.welcome();
        
        assertThat(response.message()).startsWith(WELCOME_PREFIX);
        assertThat(response.message()).contains(IP_ADDRESS_TEXT + " " + VALID_IP);
    }

    @Test
    void welcome_WhenIpAddressResolutionFails_ShouldReturnFallbackIp() {
        doReturn(FALLBACK_IP).when(testController).getLocalIpAddress();
        
        ResponseDto response = testController.welcome();
        
        assertThat(response.message()).contains(IP_ADDRESS_TEXT + " " + FALLBACK_IP);
    }

    @Test
    void getLocalIpAddress_WhenCalled_ShouldReturnNonNullValue() {
        String ipAddress = testController.getLocalIpAddress();
        
        assertThat(ipAddress).isNotNull();
        assertThat(ipAddress).isNotEmpty();
    }
}
package com.drsanches.photobooth.app;

import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
        "application.notifications.email.2fa-enabled = true",
        "application.notifications.email.info-enabled = true",
        "application.2fa.actions = REGISTRATION,USERNAME_CHANGE,PASSWORD_CHANGE,EMAIL_CHANGE,DISABLE"
})
public class BaseSpringTest {

    protected static final Supplier<String> USERNAME = () -> "username-" + UUID.randomUUID().toString().substring(0, 10);
    protected static final Supplier<String> PASSWORD = () -> UUID.randomUUID().toString();
    protected static final Supplier<String> EMAIL = () -> UUID.randomUUID() + "@example.com";
    protected static final Supplier<String> NAME = () -> "name-" + UUID.randomUUID();
    protected static final Supplier<String> URL = () -> "url-" + UUID.randomUUID();
    protected static final Supplier<String> CONFIRMATION_CODE = () -> UUID.randomUUID().toString();
    protected static final Supplier<String> FCM_TOKEN = () -> UUID.randomUUID().toString();
    protected static final Supplier<byte[]> AVATAR = () -> {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    };

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SecurityFilterChain filterChain;
    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenService tokenService;

    protected MockMvc mvc;

    @BeforeEach
    protected void initialize() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                .build();
    }

    protected Token createUser(String username, String password, String email) {
        var salt = UUID.randomUUID().toString();
        var user = userIntegrationDomainService.createUser(
                username,
                email,
                credentialsHelper.encodePassword(password, salt),
                salt
        );
        return tokenService.createToken(user.getId(), user.getRole());
    }

    protected ResultActions performGetInfo(String accessToken) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/info")
                .header("Authorization", "Bearer " + accessToken));
    }
}

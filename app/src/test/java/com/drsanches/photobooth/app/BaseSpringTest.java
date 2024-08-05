package com.drsanches.photobooth.app;

import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationCodeGenerator;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.auth.service.TokenService;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
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
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private UserProfileDomainService userProfileDomainService;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenService tokenService;
    @MockBean
    protected ConfirmationCodeGenerator confirmationCodeGenerator;

    @Autowired
    protected MockMvc mvc;

    protected Token createUser(String username, String password, String email) {
        var salt = UUID.randomUUID().toString();
        var user = userAuthDomainService.createUser(
                username,
                email,
                credentialsHelper.encodePassword(password, salt),
                salt
        );
        var token = tokenService.createToken(user.getId(), user.getRole());
        userProfileDomainService.create(user.getId(), user.getUsername());
        return token;
    }

    protected String mockConfirmationCodeGenerator() {
        var confirmationCode = CONFIRMATION_CODE.get();
        when(confirmationCodeGenerator.generate()).thenReturn(confirmationCode);
        return confirmationCode;
    }

    protected ResultActions performGetInfo(String accessToken) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/account")
                .header("Authorization", "Bearer " + accessToken));
    }
}

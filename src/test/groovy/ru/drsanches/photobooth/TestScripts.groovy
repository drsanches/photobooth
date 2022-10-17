package ru.drsanches.photobooth

import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class TestScripts extends Specification {

    String userId = "userId"
    String username = "username"
    String password = "password"

    def "add a friend to the user"() {
        when:
        TestUser user = new TestUser().register().sendFriendRequest(userId)
        String token = RequestUtils.getToken(username, sha256(password))
        RequestUtils.sendFriendRequest(token, user.id)

        then:
        assert true
    }

    private static String sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8))
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length)
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b)
            if (hex.length() == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }
}

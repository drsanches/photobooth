package com.drsanches.photobooth.app.common.token.data;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findByRefreshToken(String refreshToken);

    List<Token> findByUserId(String userId);
}

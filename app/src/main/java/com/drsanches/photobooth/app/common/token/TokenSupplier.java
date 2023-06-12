package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.common.token.data.model.Token;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class TokenSupplier {

    private Token token = null;

    public Token get() {
        return token;
    }

    void set(Token token) {
        this.token = token;
    }
}

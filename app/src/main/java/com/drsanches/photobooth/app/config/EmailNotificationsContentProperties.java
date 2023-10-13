package com.drsanches.photobooth.app.config;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "content.email-notifications")
public class EmailNotificationsContentProperties {

    private static final String SUBJECTS_FIELD = "subjects";

    private static final String TEXTS_FIELD = "texts";

    private Map<String, Map<String, String>> confirm;

    private Map<String, Map<String, String>> success;

    public void setConfirm(Map<String, Map<String, String>> confirm) {
        validateMapConsistency(confirm.get(SUBJECTS_FIELD));
        validateMapConsistency(confirm.get(TEXTS_FIELD));
        this.confirm = confirm;
    }

    public void setSuccess(Map<String, Map<String, String>> success) {
        validateMapConsistency(success.get(SUBJECTS_FIELD));
        validateMapConsistency(success.get(TEXTS_FIELD));
        this.success = success;
    }

    public String getConfirmSubject(Operation operation) {
        validateOperation(operation);
        return confirm.get(SUBJECTS_FIELD).get(operation.toString());
    }

    public String getConfirmText(Operation operation) {
        validateOperation(operation);
        return confirm.get(TEXTS_FIELD).get(operation.toString());
    }

    public String getSuccessSubject(Operation operation) {
        validateOperation(operation);
        return success.get(SUBJECTS_FIELD).get(operation.toString());
    }

    public String getSuccessText(Operation operation) {
        validateOperation(operation);
        return success.get(TEXTS_FIELD).get(operation.toString());
    }

    private void validateMapConsistency(Map<String, String> map) {
        List<String> operations = Arrays.stream(Operation.values())
                .filter(Operation::isNotificationsEnabled)
                .map(Operation::toString)
                .toList();

        if (map.keySet().size() != operations.size()
                || !map.keySet().containsAll(operations)) {
            throw ServerError.createWithMessage("Invalid content.email-notifications configuration");
        }
    }

    private void validateOperation(Operation operation) {
        if (!operation.isNotificationsEnabled()) {
            throw new ServerError("Operation " + operation + "has no notification content");
        }
    }
}

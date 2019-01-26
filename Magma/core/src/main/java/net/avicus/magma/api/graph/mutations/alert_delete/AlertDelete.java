package net.avicus.magma.api.graph.mutations.alert_delete;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;

import java.util.Map;

/**
 * Autogenerated return type of AlertDelete
 */
public class AlertDelete extends AbstractResponse<AlertDelete> {

    public AlertDelete(JsonObject fields) throws SchemaViolationError {
        for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
            String key = field.getKey();
            String fieldName = getFieldName(key);
            switch (fieldName) {
                case "clientMutationId": {
                    String optional1 = null;
                    if (!field.getValue().isJsonNull()) {
                        optional1 = jsonAsString(field.getValue(), key);
                    }

                    responseData.put(key, optional1);

                    break;
                }

                case "success": {
                    responseData.put(key, jsonAsBoolean(field.getValue(), key));

                    break;
                }

                case "__typename": {
                    responseData.put(key, jsonAsString(field.getValue(), key));
                    break;
                }
                default: {
                    throw new SchemaViolationError(this, key, field.getValue());
                }
            }
        }
    }

    public String getGraphQlTypeName() {
        return "AlertDeletePayload";
    }

    /**
     * A unique identifier for the client performing the mutation.
     */

    public String getClientMutationId() {
        return (String) get("clientMutationId");
    }

    public AlertDelete setClientMutationId(String arg) {
        optimisticData.put(getKey("clientMutationId"), arg);
        return this;
    }

    /**
     * If the alert was delete.
     */

    public Boolean isSuccess() {
        return (Boolean) get("success");
    }

    public AlertDelete setSuccess(Boolean arg) {
        optimisticData.put(getKey("success"), arg);
        return this;
    }

    public boolean unwrapsToObject(String key) {
        switch (getFieldName(key)) {
            case "clientMutationId":
                return false;

            case "success":
                return false;

            default:
                return false;
        }
    }
}

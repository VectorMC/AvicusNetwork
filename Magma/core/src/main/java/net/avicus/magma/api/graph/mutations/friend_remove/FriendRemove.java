package net.avicus.magma.api.graph.mutations.friend_remove;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;

import java.util.Map;

import net.avicus.magma.api.graph.mutations.friend_remove.remove_response_data.RemoveResponseData;

/**
 * Autogenerated return type of FriendRemove
 */
public class FriendRemove extends AbstractResponse<FriendRemove> {

    public FriendRemove(JsonObject fields) throws SchemaViolationError {
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

                case "response_data": {
                    responseData.put(key, new RemoveResponseData(jsonAsObject(field.getValue(), key)));

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
        return "FriendRemovePayload";
    }

    /**
     * A unique identifier for the client performing the mutation.
     */

    public String getClientMutationId() {
        return (String) get("clientMutationId");
    }

    public FriendRemove setClientMutationId(String arg) {
        optimisticData.put(getKey("clientMutationId"), arg);
        return this;
    }

    /**
     * Information about what the request accompanied (removed, request canceled, not friends, etc).
     */

    public RemoveResponseData getResponseData() {
        return (RemoveResponseData) get("response_data");
    }

    public FriendRemove setResponseData(RemoveResponseData arg) {
        optimisticData.put(getKey("response_data"), arg);
        return this;
    }

    /**
     * If the friend was removed.
     */

    public Boolean isSuccess() {
        return (Boolean) get("success");
    }

    public FriendRemove setSuccess(Boolean arg) {
        optimisticData.put(getKey("success"), arg);
        return this;
    }

    public boolean unwrapsToObject(String key) {
        switch (getFieldName(key)) {
            case "clientMutationId":
                return false;

            case "response_data":
                return true;

            case "success":
                return false;

            default:
                return false;
        }
    }
}

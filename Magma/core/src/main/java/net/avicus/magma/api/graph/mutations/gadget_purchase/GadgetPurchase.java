package net.avicus.magma.api.graph.mutations.gadget_purchase;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;

import java.util.Map;

import net.avicus.magma.api.graph.mutations.gadget_purchase.fail_reason.FailReason;

/**
 * Autogenerated return type of GadgetPurchase
 */
public class GadgetPurchase extends AbstractResponse<GadgetPurchase> {

    public GadgetPurchase(JsonObject fields) throws SchemaViolationError {
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

                case "fail_reason": {
                    responseData.put(key, new FailReason(jsonAsObject(field.getValue(), key)));

                    break;
                }

                case "failed": {
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
        return "GadgetPurchasePayload";
    }

    /**
     * A unique identifier for the client performing the mutation.
     */

    public String getClientMutationId() {
        return (String) get("clientMutationId");
    }

    public GadgetPurchase setClientMutationId(String arg) {
        optimisticData.put(getKey("clientMutationId"), arg);
        return this;
    }

    /**
     * Information about why the purchase failed.
     */

    public FailReason getFailReason() {
        return (FailReason) get("fail_reason");
    }

    public GadgetPurchase setFailReason(FailReason arg) {
        optimisticData.put(getKey("fail_reason"), arg);
        return this;
    }

    /**
     * If the purchase failed.
     */

    public Boolean isFailed() {
        return (Boolean) get("failed");
    }

    public GadgetPurchase setFailed(Boolean arg) {
        optimisticData.put(getKey("failed"), arg);
        return this;
    }

    public boolean unwrapsToObject(String key) {
        switch (getFieldName(key)) {
            case "clientMutationId":
                return false;

            case "fail_reason":
                return true;

            case "failed":
                return false;

            default:
                return false;
        }
    }
}

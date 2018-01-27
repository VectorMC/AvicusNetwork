package net.avicus.magma.api.graph.mutations.present_find;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;
import java.util.Map;

/**
 * Autogenerated return type of PresentFind
 */
public class PresentFind extends AbstractResponse<PresentFind> {

  public PresentFind(JsonObject fields) throws SchemaViolationError {
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

        case "message": {
          responseData.put(key, jsonAsString(field.getValue(), key));

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
    return "PresentFindPayload";
  }

  /**
   * A unique identifier for the client performing the mutation.
   */

  public String getClientMutationId() {
    return (String) get("clientMutationId");
  }

  public PresentFind setClientMutationId(String arg) {
    optimisticData.put(getKey("clientMutationId"), arg);
    return this;
  }

  /**
   * Message to be displayed to the player.
   */

  public String getMessage() {
    return (String) get("message");
  }

  public PresentFind setMessage(String arg) {
    optimisticData.put(getKey("message"), arg);
    return this;
  }

  /**
   * If the present was successfully marked as found.
   */

  public Boolean isSuccess() {
    return (Boolean) get("success");
  }

  public PresentFind setSuccess(Boolean arg) {
    optimisticData.put(getKey("success"), arg);
    return this;
  }

  public boolean unwrapsToObject(String key) {
    switch (getFieldName(key)) {
      case "clientMutationId":
        return false;

      case "message":
        return false;

      case "success":
        return false;

      default:
        return false;
    }
  }
}

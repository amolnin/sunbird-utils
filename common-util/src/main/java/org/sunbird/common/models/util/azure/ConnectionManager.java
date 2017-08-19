/**
 * 
 */
package org.sunbird.common.models.util.azure;

import java.util.Locale;

import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * This class will manage azure connection.
 * @author Manzarul
 *
 */
public class ConnectionManager {

  private static String            accountName = "sunbirddev";
  private static String            accountKey  =
      "zl8q5yA4fxG5PAha2rc6rRWQ8vQlKMGKb79YlQN1br7EOOD8jmVhBOlTP3J7KqKTj3J2TxkJArQR8IrXV551Fg==";
  private static String            storageAccountString;
  private static ConnectionManager connectionManager;
  static {
    String name = System.getenv(JsonKey.ACCOUNT_NAME);
    String key = System.getenv(JsonKey.ACCOUNT_KEY);
    if (ProjectUtil.isStringNullOREmpty(name)
        || ProjectUtil.isStringNullOREmpty(key)) {
      ProjectLogger.log(
          "Azure account name and key is not provided by environment variable."
              + name + " " + key);
      storageAccountString = "DefaultEndpointsProtocol=https;AccountName="
          + accountName + ";AccountKey=" + accountKey
          + ";EndpointSuffix=core.windows.net";
    } else {
      accountName = name;
      accountKey = key;
      ProjectLogger.log(
          "Azure account name and key is  provided by environment variable."
              + name + " " + key);
      storageAccountString = "DefaultEndpointsProtocol=https;AccountName="
          + accountName + ";AccountKey=" + accountKey
          + ";EndpointSuffix=core.windows.net";
    }
  }

  private ConnectionManager() throws CloneNotSupportedException {
    if (connectionManager != null)
      throw new CloneNotSupportedException();
  }

  /**
   * This method will provide Azure CloudBlobContainer object or in case of error it will provide null;
   * 
   * @param containerName String
   * @return CloudBlobContainer or null
   */
  public static CloudBlobContainer getContainer(String containerName) {
    // Retrieve storage account from connection-string.
    try {
      CloudStorageAccount storageAccount =
          CloudStorageAccount.parse(storageAccountString);
      CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
      // Get a reference to a container.
      // The container name must be lower case
      CloudBlobContainer container = cloudBlobClient
          .getContainerReference(containerName.toLowerCase(Locale.ENGLISH));
      // Create the container if it does not exist.
      boolean response = container.createIfNotExists();
      ProjectLogger.log("container creation done if not exist==" + response);
      // Create a permissions object.
      BlobContainerPermissions containerPermissions =
          new BlobContainerPermissions();
      // Include public access in the permissions object.
      containerPermissions
          .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
      // Set the permissions on the container.
      container.uploadPermissions(containerPermissions);
      return container;
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
    return null;
  }

}

# IBM Cloud Object Storage - Java SDK

This package allows Java developers to write software that interacts with [IBM
Cloud Object Storage](https://console.bluemix.net/docs/services/cloud-object-storage/about-cos.html). It is a fork of [the ``AWS SDK for Java`` library](https://github.com/aws/aws-sdk-java).

## Documentation

* [Core documentation for IBM COS](https://console.bluemix.net/docs/services/cloud-object-storage/getting-started.html)
* [Java API reference documentation](https://ibm.github.io/ibm-cos-sdk-java)
* [REST API reference documentation](https://console.bluemix.net/docs/services/cloud-object-storage/api-reference/about-compatibility-api.html)

Changes to the SDK are tracked in the [CHANGELOG.md][changes-file] file.

* [Example code](#example-code)
* [Building from source](#building-from-source)
* [Getting help](#getting-help)

## Quick start

You'll need:
  * An instance of COS.
  * An API key from [IBM Cloud Identity and Access Management](https://console.bluemix.net/docs/iam/users_roles.html) with at least `Writer` permissions.
  * The ID of the instance of COS that you are working with.
  * Token acquisition endpoint
  * Service endpoint
  * **Java 1.6+**.

These values can be found in the Bluemix UI by [generating a 'service credential'](https://console.bluemix.net/docs/services/cloud-object-storage/iam/service-credentials.html).

## Getting the SDK
The recommended way to use the IBM COS SDK for Java in your project is to consume it from Maven. Import the `ibm-cos-java-sdk` and specify the SDK Maven modules that your project needs in the dependencies:

```xml
<dependencies>
  <dependency>
    <groupId>com.ibm.cos</groupId>
    <artifactId>ibm-cos-java-sdk</artifactId>
  </dependency>
</dependencies>
```

## Migrating from 1.x.x
The 2.0 release of the SDK introduces a namespacing change that allows an application to make use of the original AWS library to connect to AWS resources within the same application or environment. To migrate from 1.x to 2.0 some changes are necessary:

1. Update using Maven by changing all  `ibm-cos-java-sdk` dependency version tags to  `2.0.0` in the pom.xml. Verify that there are no SDK module dependencies in the pom.xml with a version earlier than  `2.0.0`.
2. Update any import declarations from `amazonaws` to `ibm.cloud.objectstorage`.

## Example code (Version 2.x)

```java
package com.cos;

import java.sql.Timestamp;
import java.util.List;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.Bucket;
import com.ibm.cloud.objectstorage.services.s3.model.ListObjectsRequest;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectListing;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectSummary;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;

public class CosExample
{

    private static AmazonS3 _s3Client;

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        SDKGlobalConfiguration.IAM_ENDPOINT = "https://iam.bluemix.net/oidc/token";

        String bucketName = "<bucketName>";
        String api_key = "<apiKey>";
        String service_instance_id = "<resourceInstanceId>";
        String endpoint_url = "https://s3-api.us-geo.objectstorage.softlayer.net";
        String location = "us";

        System.out.println("Current time: " + new Timestamp(System.currentTimeMillis()).toString());
        _s3Client = createClient(api_key, service_instance_id, endpoint_url, location);
        listObjects(bucketName, _s3Client);
        listBuckets(_s3Client);
    }

    /**
     * @param bucketName
     * @param clientNum
     * @param api_key
     *            (or access key)
     * @param service_instance_id
     *            (or secret key)
     * @param endpoint_url
     * @param location
     * @return AmazonS3
     */
    public static AmazonS3 createClient(String api_key, String service_instance_id, String endpoint_url, String location)
    {
        AWSCredentials credentials;
        if (endpoint_url.contains("objectstorage.softlayer.net")) {
            credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
        } else {
            String access_key = api_key;
            String secret_key = service_instance_id;
            credentials = new BasicAWSCredentials(access_key, secret_key);
        }
        ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(5000);
        clientConfig.setUseTcpKeepAlive(true);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new EndpointConfiguration(endpoint_url, location)).withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfig).build();
        return s3Client;
    }

    /**
     * @param bucketName
     * @param s3Client
     */
    public static void listObjects(String bucketName, AmazonS3 s3Client)
    {
        System.out.println("Listing objects in bucket " + bucketName);
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();
    }

    /**
     * @param s3Client
     */
    public static void listBuckets(AmazonS3 s3Client)
    {
        System.out.println("Listing buckets");
        final List<Bucket> bucketList = _s3Client.listBuckets();
        for (final Bucket bucket : bucketList) {
            System.out.println(bucket.getName());
        }
        System.out.println();
    }

}
```

## Building from source

Once you check out the code from GitHub, you can build it using Maven:

```sh
mvn clean install
```

## Getting help
Feel free to use GitHub issues for tracking bugs and feature requests, but for help please use one of the following resources:

* Read a quick start guide in [Bluemix Docs][bluemix-docs]
* Ask a question on [StackOverflow][stack-overflow] and tag it with `ibm` and `object-storage`
* Open a support ticket with [IBM Bluemix Support][ibm-bluemix-support]
* If it turns out that you may have found a bug, please [open an issue][open-an-issue]


[changes-file]: ./CHANGELOG.md
[bluemix-docs]: https://console.bluemix.net/docs/services/cloud-object-storage/libraries/java.html#java
[stack-overflow]: http://stackoverflow.com/questions/tagged/object-storage+ibm
[ibm-bluemix-support]: https://support.ng.bluemix.net/gethelp/
[open-an-issue]: https://github.com/ibm/ibm-cos-sdk-java/issues/new

## License

This SDK is distributed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0),
see LICENSE.txt and NOTICE.txt for more information.

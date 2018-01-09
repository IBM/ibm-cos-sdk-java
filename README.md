# IBM Cloud Object Storage - Java SDK

This package allows Java developers to write software that interacts with [IBM
Cloud Object Storage](https://console.bluemix.net/docs/services/cloud-object-storage/about-cos.html). It is a fork of [the ``AWS SDK for Javascript`` library](https://github.com/aws/aws-sdk-js).

## Documentation

* [Core documentation for IBM COS](https://console.bluemix.net/docs/services/cloud-object-storage/getting-started.html)
* [Python API reference documentation](https://ibm.github.io/ibm-cos-sdk-java)
* [REST API reference documentation](https://console.bluemix.net/docs/services/cloud-object-storage/api-reference/about-compatibility-api.html)

Changes to the SDK are tracked in the [CHANGELOG.md][changes-file] file.

* [Example code](#example-code)
* [Building from source](#building-from-source)
* [Getting help](#getting-help)

## Quick start

You'll need:
  * An instance of COS.
  * An API key from [IBM Cloud Identity and Access Management](console.bluemix.net/docs/iam/users_roles.html) with at least `Writer` permissions.
  * The ID of the instance of COS that you are working with.
  * Token acquisition endpoint
  * Service endpoint
  * **Java 1.6+**.

These values can be found in the Bluemix UI by [generating a 'service credential'](console.bluemix.net/docs/services/cloud-object-storage/iam/service-credentials.html).

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


## Example code

```java
package com.cos;

import java.sql.Timestamp;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ibm.oauth.BasicIBMOAuthCredentials;

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

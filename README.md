# IBM Cloud Object Storage - Java SDK

This package allows Java developers to write software that interacts with [IBM
Cloud Object Storage](https://cloud.ibm.com/docs/services/cloud-object-storage/about-cos.html). It is a fork of [the ``AWS SDK for Java`` library](https://github.com/aws/aws-sdk-java).

## Documentation

* [Core documentation for IBM COS](https://cloud.ibm.com/docs/services/cloud-object-storage/getting-started.html)
* [Code Examples](https://cloud.ibm.com/docs/services/cloud-object-storage?topic=cloud-object-storage-using-java#examples)
* [Java API reference documentation](https://ibm.github.io/ibm-cos-sdk-java)
* [REST API reference documentation](https://cloud.ibm.com/docs/services/cloud-object-storage/api-reference/about-api.html)

Changes to the SDK are tracked in the [CHANGELOG.md][changes-file] file.

* [Building from source](#building-from-source)
* [Getting help](#getting-help)

## Quick start

You'll need:
  * An instance of COS.
  * An API key from [IBM Cloud Identity and Access Management](https://cloud.ibm.com/docs/iam/users_roles.html) with at least `Writer` permissions.
  * The ID of the instance of COS that you are working with.
  * Token acquisition endpoint
  * Service endpoint
  * **Java 1.6+**.

These values can be found in the Bluemix UI by [generating a 'service credential'](https://cloud.ibm.com/docs/services/cloud-object-storage/iam/service-credentials.html).

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

## Building from source

Once you check out the code from GitHub, you can build it using Maven:

```sh
mvn clean install
```

## Using a Service Credential
From Release 2.1.0 you can source credentials directly from a 
[Service Credential](https://cloud.ibm.com/docs/services/cloud-object-storage/iam/service-credentials.html) JSON document 
generated in the IBM Cloud console saved to `~/.bluemix/cos_credentials`. The SDK will automatically load these providing you 
have not explicitly set other credentials during client creation. 
If the Service Credential contain [HMAC keys](https://cloud.ibm.com/docs/services/cloud-object-storage/hmac/credentials.html) 
the client will use those and authenticate using a signature, otherwise the client will use the provided API key to authenticate 
using bearer tokens.

## Aspera high-speed transfer
It is now possible to use the IBM Aspera high-speed transfer service as an alternative method to managed transfers of larger objects. The Aspera high-speed transfer service is especially effective across long distances or in environments with high rates of packet loss. For more details, check out the [IBM Cloud documentation](https://cloud.ibm.com/docs/services/cloud-object-storage/basics/aspera.html#using-libraries-and-sdks).

## Archive Tier Support
You can automatically archive objects after a specified length of time or after a specified date.  Once archived, a temporary copy of an object can be restored for access as needed.  Restore time may take up to 15 hours.

An archive policy is set at the bucket level by calling the `setBucketLifecycleConfiguration` method on a client instance.  A newly added or modified archive policy applies to new objects uploaded and does not affect existing objects.  For more detail, [see the documentation](https://cloud.ibm.com/docs/services/cloud-object-storage/libraries/java.html#java).

## Immutable Object Storage
Users can configure buckets with an Immutable Object Storage policy to prevent objects from being modified or deleted for a defined period of time.
The retention period can be specified on a per-object basis, or objects can inherit a default retention period set on the bucket. It is also possible
to set open-ended and permanent retention periods. Immutable Object Storage meets the rules set forth by the SEC governing record retention, and 
IBM Cloud administrators are unable to bypass these restrictions. For more detail, [see the documentation][immutable-storage-docs]. 
Immutable Object Storage does not support Aspera transfers via the SDK to upload objects or directories at this stage.

## Getting help
Feel free to use GitHub issues for tracking bugs and feature requests, but for help please use one of the following resources:

* Read a quick start guide in [IBM Cloud Docs][bluemix-docs]
* Ask a question on [StackOverflow][stack-overflow] and tag it with `ibm` and `object-storage`
* Open a support ticket with [IBM Cloud Support][ibm-bluemix-support]
* If it turns out that you may have found a bug, please [open an issue][open-an-issue]


[changes-file]: ./CHANGELOG.md
[bluemix-docs]: https://cloud.ibm.com/docs/services/cloud-object-storage/libraries/java.html#java
[stack-overflow]: http://stackoverflow.com/questions/tagged/object-storage+ibm
[ibm-bluemix-support]: https://cloud.ibm.com/unifiedsupport/supportcenter
[open-an-issue]: https://github.com/ibm/ibm-cos-sdk-java/issues/new
[immutable-storage-docs]: https://cloud.ibm.com/docs/services/cloud-object-storage/basics/immutable.html

## Deprecation Notice

Deprecation Notice for IBM Cloud Object Storage Java and Python SDK Versions 1.x

As of March 31, 2018, IBM will no longer add new features to the IBM Cloud Object Storage Java SDK Versions 1.x and the IBM Cloud Object Storage Python SDK Versions 1.x.  We will continue to provide critical bug fixes to the 1.x releases through May 31, 2018.

Versions 1.x for Java and Python SDK will no longer be supported after May 31, 2018.

If you are using the 1.x version of the Java or Python SDK, please upgrade to the latest IBM Cloud Object Storage SDK versions 2.x.  Instructions on how to upgrade from SDK Java and Python 1.x can be found in the "Migrating from 1.x.x" section of corresponding Readme.

Note: The IBM Cloud Object Storage Node.js SDK is NOT affected by this change.

For questions, please open an issue:

[Java](https://github.com/ibm/ibm-cos-sdk-java/issues/new)

[Python](https://github.com/ibm/ibm-cos-sdk-python/issues/new)


## Migrating from 1.x.x
The 2.0 release of the SDK introduces a namespacing change that allows an application to make use of the original AWS library to connect to AWS resources within the same application or environment. To migrate from 1.x to 2.0 some changes are necessary:

1. Update using Maven by changing all  `ibm-cos-java-sdk` dependency version tags to  `2.0.0` in the pom.xml. Verify that there are no SDK module dependencies in the pom.xml with a version earlier than  `2.0.0`.
2. Update any import declarations from `amazonaws` to `ibm.cloud.objectstorage`.

## License

This SDK is distributed under the
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0),
see [LICENSE.txt][LICENSE.txt] and [NOTICE.txt][NOTICE.txt] for more information.

[LICENSE.txt]: ./LICENSE.txt
[NOTICE.txt]: ./NOTICE.txt

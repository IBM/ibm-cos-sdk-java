# IBM COS SDK for Java

The **IBM COS SDK for Java** enables Java developers to work with IBM Cloud Object Storage] to
build scalable solutions. You can get
started in minutes using ***Maven*** or by downloading a [single zip file]().

* [Getting Help](#getting-help)

## Release Notes ##
Changes to the SDK are tracked in the [CHANGELOG.md][changes-file] file.

## Getting Started

#### Sign up for IBM Cloud ####

Before you begin, you need a IBM Cloud account and an instance of COS.

#### Minimum requirements ####

To run the SDK you will need **Java 1.6+**.

#### Install the SDK ####

The recommended way to use the IBM COS SDK for Java in your project is to consume it from Maven. Import
the `ibm-cos-java-sdk` and specify the SDK Maven modules that your project needs in the
dependencies:

```xml
<dependencies>
  <dependency>
    <groupId>ibm-cos</groupId>
    <artifactId>ibm-cos-java-sdk</artifactId>
  </dependency>
</dependencies>
```


## Building From Source

Once you check out the code from GitHub, you can build it using Maven:

```sh
mvn clean install
```

## Getting Help
Please use these community resources for getting help. We use GitHub issues for tracking bugs and feature requests and have limited bandwidth to address them.

* Ask a question on [StackOverflow][stack-overflow] and tag it with `ibm` and `object-storage`


[changes-file]: ./CHANGELOG.md
[stack-overflow]: http://stackoverflow.com/questions/tagged/object-storage+ibm

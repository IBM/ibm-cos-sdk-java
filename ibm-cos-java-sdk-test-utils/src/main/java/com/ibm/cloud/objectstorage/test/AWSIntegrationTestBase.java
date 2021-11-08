/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.ibm.cloud.objectstorage.test;

import com.ibm.cloud.objectstorage.test.retry.RetryRule;
import java.io.InputStream;

import org.junit.BeforeClass;

import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentialsProviderChain;
import com.ibm.cloud.objectstorage.auth.EnvironmentVariableCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.PropertiesFileCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.SystemPropertiesCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.profile.ProfileCredentialsProvider;
import com.ibm.cloud.objectstorage.util.IOUtils;
import org.junit.Rule;

public abstract class AWSIntegrationTestBase {

    /**
     * Shared AWS credentials, loaded from a properties file.
     */
    private static AWSCredentials credentials;

    /** Default Properties Credentials file path */
    private static final String propertiesFilePath = System.getProperty("user.home")
            + "/.aws/awsTestAccount.properties";

    private static final String TEST_CREDENTIALS_PROFILE_NAME = "ibm-cos-java-sdk-test";

    private static final AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(
            new PropertiesFileCredentialsProvider(propertiesFilePath),
            new ProfileCredentialsProvider(TEST_CREDENTIALS_PROFILE_NAME), new EnvironmentVariableCredentialsProvider(),
            new SystemPropertiesCredentialsProvider());

    @Rule
    public RetryRule retry = new RetryRule(3);

    /**
     * Before of super class is guaranteed to be called before that of a subclass so the following
     * is safe. http://junit-team.github.io/junit/javadoc/latest/org/junit/Before.html
     */
    @BeforeClass
    public static void setUpCredentials() {
        if (credentials == null) {
            try {
                credentials = chain.getCredentials();
            } catch (Exception ignored) {
            }
        }
    }

    protected void setRetryRule(RetryRule retry) {
        this.retry = retry;
    }

    /**
     * @return AWSCredentials to use during tests. Setup by base fixture
     */
    protected static AWSCredentials getCredentials() {
        return credentials;
    }

    /**
     * Reads a system resource fully into a String
     * 
     * @param location
     *            Relative or absolute location of system resource.
     * @return String contents of resource file
     * @throws RuntimeException
     *             if any error occurs
     */
    protected String getResourceAsString(String location) {
        try {
            InputStream resourceStream = getClass().getResourceAsStream(location);
            String resourceAsString = IOUtils.toString(resourceStream);
            resourceStream.close();
            return resourceAsString;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

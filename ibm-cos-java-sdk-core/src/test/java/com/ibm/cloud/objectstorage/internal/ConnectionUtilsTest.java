/*
 * Copyright (c) 2017. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ibm.cloud.objectstorage.internal;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.IsCloseTo.closeTo;

import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.hamcrest.number.IsCloseTo;
import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import utils.EnvironmentVariableHelper;

public class ConnectionUtilsTest {

    @ClassRule
    public static WireMockRule mockProxyServer = new WireMockRule(0);

    @Rule
    public WireMockRule mockServer = new WireMockRule(0);

    private final ConnectionUtils sut = ConnectionUtils.getInstance();

    @After
    public void cleanup() {
        System.getProperties().remove("http.proxyHost");
        System.getProperties().remove("http.proxyPort");
    }

    @Test
    public void proxiesAreNotUsedEvenIfPropertyIsSet() throws IOException {

        System.getProperties().put("http.proxyHost", "localhost");
        System.getProperties().put("http.proxyPort", String.valueOf(mockProxyServer.port()));
        HttpURLConnection connection = sut.connectToEndpoint(URI.create("http://localhost:" + mockServer.port()), new HashMap<String, String>());

        assertThat(connection.getRequestMethod(), is("GET"));
        assertThat(connection.usingProxy(), is(false));
    }

    @Test
    public void headersArePassedAsPartOfRequest() throws IOException {
        HttpURLConnection connection = sut.connectToEndpoint(URI.create("http://localhost:" + mockServer.port()), Collections.singletonMap("HeaderA", "ValueA"));
        connection.getResponseCode();
        mockServer.verify(getRequestedFor(urlMatching("/")).withHeader("HeaderA", equalTo("ValueA")));
    }

    @Test
    public void defaultTimeoutIsCorrect() {
        assertThat(sut.getTimeoutMillis(), is(1000));
    }

    @Test
    public void intTimeoutIsHonored() {
        EnvironmentVariableHelper envVarHelper = new EnvironmentVariableHelper();
        try {
            envVarHelper.set(SDKGlobalConfiguration.AWS_METADATA_SERVICE_TIMEOUT_ENV_VAR, "10");
            ConnectionUtils connectionUtils = new ConnectionUtils();
            assertThat(connectionUtils.getTimeoutMillis(), is(10000));
        } finally {
            envVarHelper.reset();
        }
    }

    @Test
    public void doubleTimeoutIsHonored() {
        EnvironmentVariableHelper envVarHelper = new EnvironmentVariableHelper();
        try {
            envVarHelper.set(SDKGlobalConfiguration.AWS_METADATA_SERVICE_TIMEOUT_ENV_VAR, "10.5");
            ConnectionUtils connectionUtils = new ConnectionUtils();
            assertThat(connectionUtils.getTimeoutMillis(), allOf(greaterThan(10490), lessThan(10510)));
        } finally {
            envVarHelper.reset();
        }
    }

    @Test
    public void zeroIntTimeoutFails() {
        assertThatTimeoutValueFails("0");
    }

    @Test
    public void zeroDoubleTimeoutFails() {
        assertThatTimeoutValueFails("0.0");
    }

    @Test
    public void negativeIntTimeoutFails() {
        assertThatTimeoutValueFails("-1");
    }

    @Test
    public void negativeDoubleTimeoutFails() {
        assertThatTimeoutValueFails("-1.0");
    }

    @Test
    public void tooLargeDoubleValueFails() {
        assertThatTimeoutValueFails(Double.toString(Double.MAX_VALUE));
    }

    @Test
    public void tooLargeIntValueFails() {
        assertThatTimeoutValueFails(Long.toString((long) Integer.MAX_VALUE + 1L));
    }

    private void assertThatTimeoutValueFails(String value) {
        EnvironmentVariableHelper envVarHelper = new EnvironmentVariableHelper();
        try {
            envVarHelper.set(SDKGlobalConfiguration.AWS_METADATA_SERVICE_TIMEOUT_ENV_VAR, value);
            new ConnectionUtils();
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected
        } finally {
            envVarHelper.reset();
        }
    }
}
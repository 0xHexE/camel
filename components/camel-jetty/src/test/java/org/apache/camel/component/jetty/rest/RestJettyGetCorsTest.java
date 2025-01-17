/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jetty.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.apache.camel.spi.RestConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestJettyGetCorsTest extends BaseJettyTest {

    @Test
    public void testCors() throws Exception {
        // send OPTIONS first which should not be routed
        getMockEndpoint("mock:input").expectedMessageCount(0);

        Exchange out = template.request("http://localhost:" + getPort() + "/users/123/basic", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, "OPTIONS");
            }
        });

        assertEquals(RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_ORIGIN,
                out.getMessage().getHeader("Access-Control-Allow-Origin"));
        assertEquals(RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_METHODS,
                out.getMessage().getHeader("Access-Control-Allow-Methods"));
        assertEquals(RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_HEADERS,
                out.getMessage().getHeader("Access-Control-Allow-Headers"));
        assertEquals(RestConfiguration.CORS_ACCESS_CONTROL_MAX_AGE, out.getMessage().getHeader("Access-Control-Max-Age"));

        assertMockEndpointsSatisfied();

        resetMocks();
        getMockEndpoint("mock:input").expectedMessageCount(1);

        // send GET request which should be routed

        String out2 = template.requestBody("http://localhost:" + getPort() + "/users/123/basic", null, String.class);
        assertEquals("123;Donald Duck", out2);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // configure to use jetty on localhost with the given port
                restConfiguration().component("jetty").host("localhost").port(getPort()).enableCORS(true);

                // use the rest DSL to define the rest services
                rest("/users/").get("{id}/basic").to("direct:basic");
                from("direct:basic").to("mock:input").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("id", String.class);
                        exchange.getMessage().setBody(id + ";Donald Duck");
                    }
                });
            }
        };
    }

}

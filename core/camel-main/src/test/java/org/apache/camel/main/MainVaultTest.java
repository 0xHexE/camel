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
package org.apache.camel.main;

import org.apache.camel.CamelContext;
import org.apache.camel.vault.AwsVaultConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainVaultTest {

    @Test
    public void testMain() throws Exception {
        Main main = new Main();

        main.addInitialProperty("camel.vault.aws.accessKey", "myKey");
        main.addInitialProperty("camel.vault.aws.secretKey", "mySecret");
        main.addInitialProperty("camel.vault.aws.region", "myRegion");

        main.start();

        CamelContext context = main.getCamelContext();
        assertNotNull(context);

        AwsVaultConfiguration cfg = context.getVaultConfiguration().aws();
        assertNotNull(cfg);

        Assertions.assertEquals("myKey", cfg.getAccessKey());
        Assertions.assertEquals("mySecret", cfg.getSecretKey());
        Assertions.assertEquals("myRegion", cfg.getRegion());

        main.stop();
    }

    @Test
    public void testMainFluent() throws Exception {
        Main main = new Main();
        main.configure().vault().aws()
                .withAccessKey("myKey")
                .withSecretKey("mySecret")
                .withRegion("myRegion")
                .end();

        main.start();

        CamelContext context = main.getCamelContext();
        assertNotNull(context);

        AwsVaultConfiguration cfg = context.getVaultConfiguration().aws();
        assertNotNull(cfg);

        Assertions.assertEquals("myKey", cfg.getAccessKey());
        Assertions.assertEquals("mySecret", cfg.getSecretKey());
        Assertions.assertEquals("myRegion", cfg.getRegion());

        main.stop();
    }

}

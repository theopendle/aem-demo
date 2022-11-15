/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.theopendle.core.models;

import com.day.cq.wcm.api.Page;
import com.theopendle.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
@ExtendWith(AemContextExtension.class)
class HelloWorldImplTest {

    private final AemContext context = AppAemContext.newAemContext();

    private HelloWorldImpl model;

    @BeforeEach
    public void setup() {

        // prepare a page with a test resource
        final Page page = context.create().page("/content/mypage");
        context.create().resource(page, "hello",
                "sling:resourceType", "demo/components/helloworld");

        model = context.request().adaptTo(HelloWorldImpl.class);
    }

    @Test
    void testGetMessage() {
        // some very basic junit tests
        final String msg = model.getMessage();
        assertNotNull(msg);
        assertEquals("Hello world", msg);
    }
}

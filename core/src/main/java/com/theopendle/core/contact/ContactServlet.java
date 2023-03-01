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
package com.theopendle.core.contact;

import com.theopendle.core.servlets.RestServlet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;

@Slf4j
@Component(service = {Servlet.class})
@SlingServletPaths("/bin/contact")
@ServiceDescription("Contact Servlet")
public class ContactServlet extends RestServlet<ContactServlet.Request, ContactServlet.Response> {

    private static final long serialVersionUID = 1L;

    @Reference
    private transient ContactService service;

    @Override
    public Class<Request> getRequestPayloadClass() {
        return Request.class;
    }

    @Override
    protected Response doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response, final Request requestPayload) {
        final String feedback = service.postContactMessage(requestPayload.getMessage());
        return new Response(feedback);
    }

    @Data
    @NoArgsConstructor
    public static class Request {
        private String message;
    }

    @Data
    @AllArgsConstructor
    public static class Response {
        private String feedback;
    }
}

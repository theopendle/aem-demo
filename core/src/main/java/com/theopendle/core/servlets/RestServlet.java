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
package com.theopendle.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class RestServlet<R, T> extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @return the class from which the JSON payload of the incoming request should be mapped.
     */
    protected abstract Class<R> getRequestPayloadClass();

    protected T doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response, final R requestPayload) throws IOException {
        handleMethodNotImplemented(request, response);
        return null;
    }

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        final String json = IOUtils.toString(request.getReader());

        final ObjectMapper objectMapper = new ObjectMapper();

        final R requestPayload = objectMapper.readValue(json, getRequestPayloadClass());
        final T responsePayload = doPost(request, response, requestPayload);

        response.getWriter().print(objectMapper.writeValueAsString(responsePayload));
    }
}

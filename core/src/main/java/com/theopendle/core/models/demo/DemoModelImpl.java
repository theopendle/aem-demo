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
package com.theopendle.core.models.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Model(
        adaptables = Resource.class,
        adapters = DemoModel.class
)
@Slf4j
public class DemoModelImpl implements DemoModel {

    @Inject
    private Resource continents;

    @Getter
    private List<Continent> continentList;

    @PostConstruct
    protected void init() {
        if (continents == null) {
            log.error("Could not find policy");
            return;
        }

        continentList = getMultifieldValues(continents, Continent.class);
    }

    private Map<String, Object> handleItem(@NonNull final Resource itemResource) {
        final Map<String, Object> object = new HashMap<>(itemResource.getValueMap());
        itemResource.getChildren().forEach(nestedMultiField ->
                object.put(nestedMultiField.getName(), handleMultiField(nestedMultiField)));
        return object;
    }

    private List<Object> handleMultiField(@NonNull final Resource multiFieldResource) {
        final List<Object> list = new ArrayList<>();
        multiFieldResource.getChildren().forEach(item ->
                list.add(handleItem(item)));
        return list;
    }

    private <T> List<T> getMultifieldValues(@NonNull final Resource multiField, @NonNull final Class<T> type) {

        final ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final List<Object> list = handleMultiField(multiField);
        return list.stream()
                .map(child -> mapper.convertValue(child, type))
                .collect(Collectors.toList());
    }
}

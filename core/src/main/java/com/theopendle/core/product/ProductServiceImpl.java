package com.theopendle.core.product;

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component(service = ProductService.class, immediate = true)
public class ProductServiceImpl implements ProductService {

    private static final Map<String, List<String>> DATA = Map.of(
            "shoes", List.of("boots", "sandals"),
            "tops", List.of("shirt", "sweater")
    );

    @Override
    public List<String> getProducts(final String category) {

        if (category == null) {
            return DATA.entrySet()
                    .stream()
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toList());
        }

        return DATA.getOrDefault(category, null);
    }
}

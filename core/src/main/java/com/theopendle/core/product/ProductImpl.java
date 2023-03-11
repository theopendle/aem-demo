package com.theopendle.core.product;

import com.theopendle.core.injectors.request.RequestParameter;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = Product.class,
        resourceType = ProductImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ProductImpl implements Product {

    public static final String RESOURCE_TYPE = "demo/components/product";

    @OSGiService
    private ProductService service;

    @Getter
    @RequestParameter
    private String category;

    @Getter
    private List<String> products;

    @PostConstruct
    protected void init() {
        products = service.getProducts(category);
    }
}

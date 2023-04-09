package com.theopendle.core.genericentities.form.impl;

import com.theopendle.core.genericentities.form.Proxy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Proxy.class)
public class ProxyImpl implements Proxy {

    @Override
    public String getCqDialogPath() {
        // TODO:implement
        return "/mnt/overlay/demo/entities/part" + "/form";
    }
}

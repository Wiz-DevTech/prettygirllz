package com.wizdevtech.identityaccess.service.render;

import com.wizdevtech.identityaccess.model.enums.RenderingEngine;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class RenderEngineFactory {
    private final Map<RenderingEngine, RenderEngine> engines;

    // Simply create the instance directly without using a @Bean method
    public RenderEngineFactory() {
        engines = new EnumMap<>(RenderingEngine.class);
        engines.put(RenderingEngine.UNITY, new UnityRenderEngine());
        // Add other engines as they become available
    }

    public RenderEngine getEngine(RenderingEngine type) {
        return engines.getOrDefault(type, engines.get(RenderingEngine.UNITY)); // Default to Unity
    }
}
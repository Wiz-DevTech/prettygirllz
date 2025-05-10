package com.wizdevtech.identityaccess.service.render;

import com.wizdevtech.identityaccess.model.AvatarConfig;

public interface RenderEngine {
    String renderPreview(AvatarConfig config);
    String renderAnimation(AvatarConfig config, String animationParams);
}

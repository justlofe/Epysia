package fr.epistudio.epysia.editor.ui;

import fr.epistudio.epysia.editor.inspector.AssetMimeTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AssetKinds {

    public static final List<String> MESH_PRESETS = List.of("preset:cube", "preset:plane",
            "preset:capsule", "preset:sphere", "preset:quad", "preset:unitQuad");

    private static final Map<String, Set<String>> EXTENSIONS = Map.of(
            AssetMimeTypes.MESH, Set.of(".obj", ".epymesh"),
            AssetMimeTypes.TEXTURE, Set.of(".png", ".jpg", ".jpeg", ".tga", ".bmp"),
            AssetMimeTypes.ATLAS, Set.of(".epyatlas"),
            AssetMimeTypes.INSTANCES, Set.of(".epyinstances"),
            AssetMimeTypes.MATERIAL, Set.of(".epymaterial"));

    private AssetKinds() {
    }

    public static Set<String> extensionsFor(String mimeType) {
        return EXTENSIONS.getOrDefault(mimeType, Set.of());
    }
}

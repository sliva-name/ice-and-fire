package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import com.github.alexthe666.iceandfire.IceAndFire;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class TabulaModelHandlerHelper {

    public static TabulaModelContainer loadTabulaModel(String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith(".tbl")) {
            path += ".tbl";
        }
        path = path.toLowerCase(Locale.ROOT);
        // Citadel is a separate module; its ClassLoader cannot see Ice and Fire assets.
        InputStream stream = IceAndFire.class.getResourceAsStream(path);
        if (stream == null) {
            stream = IceAndFire.class.getClassLoader().getResourceAsStream(path.substring(1));
        }
        if (stream == null) {
            stream = TabulaModelHandler.class.getResourceAsStream(path);
        }
        if (stream == null) {
            throw new FileNotFoundException("Tabula model resource not found: " + path);
        }
        return TabulaModelHandler.INSTANCE.loadTabulaModelArchive(path, stream);
    }
}

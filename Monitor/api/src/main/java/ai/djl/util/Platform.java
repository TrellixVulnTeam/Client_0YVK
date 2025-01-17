/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package main.java.ai.djl.util;

import ai.djl.util.cuda.CudaUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * The platform contains information regarding the version, os, and build flavor of the MXNet native
 * code.
 */
public final class Platform {

    private String version;
    private String osPrefix;
    private String flavor;
    private String cudaArch;
    private String[] libraries;
    private boolean placeholder;

    /** Constructor used only for {@link Platform#fromSystem()}. */
    private Platform() {}

    /**
     * Returns the platform that parsed from "engine".properties file.
     *
     * @param url the url to the "engine".properties file
     * @return the platform that parsed from mxnet.properties file
     * @throws IOException if the file could not be read
     */
    public static Platform fromUrl(URL url) throws IOException {
        Platform platform = Platform.fromSystem();
        try (InputStream conf = url.openStream()) {
            Properties prop = new Properties();
            prop.load(conf);
            // 1.6.0 later should always has version property
            platform.version = prop.getProperty("version");
            if (platform.version == null) {
                throw new IllegalArgumentException(
                        "version key is required in <engine>.properties file.");
            }
            platform.placeholder = prop.getProperty("placeholder") != null;
            String flavorPrefixedClassifier = prop.getProperty("classifier", "");
            String libraryList = prop.getProperty("libraries", "");
            if (libraryList.isEmpty()) {
                platform.libraries = new String[0];
            } else {
                platform.libraries = libraryList.split(",");
            }

            if (!flavorPrefixedClassifier.isEmpty()) {
                platform.flavor = flavorPrefixedClassifier.split("-")[0];
                platform.osPrefix = flavorPrefixedClassifier.split("-")[1];
            }
        }
        return platform;
    }

    /**
     * Returns the platform for the current system.
     *
     * @return the platform for the current system
     */
    public static Platform fromSystem() {
        Platform platform = new Platform();
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Win")) {
            platform.osPrefix = "win";
        } else if (osName.startsWith("Mac")) {
            platform.osPrefix = "osx";
        } else if (osName.startsWith("Linux")) {
            platform.osPrefix = "linux";
        } else {
            throw new AssertionError("Unsupported platform: " + osName);
        }
        if (CudaUtils.getGpuCount() > 0) {
            platform.flavor = "cu" + CudaUtils.getCudaVersionString();
            platform.cudaArch = CudaUtils.getComputeCapability(0);
        } else {
            platform.flavor = "";
        }
        return platform;
    }

    /**
     * Returns the Engine version.
     *
     * @return the Engine version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the os (win, osx, or linux).
     *
     * @return the os (win, osx, or linux)
     */
    public String getOsPrefix() {
        return osPrefix;
    }

    /**
     * Returns the MXNet build flavor.
     *
     * @return the MXNet build flavor
     */
    public String getFlavor() {
        return flavor;
    }

    /**
     * Returns the classifier for the platform.
     *
     * @return the classifier for the platform
     */
    public String getClassifier() {
        return osPrefix + "-x86_64";
    }

    /**
     * Returns the cuda arch.
     *
     * @return the cuda arch
     */
    public String getCudaArch() {
        return cudaArch;
    }

    /**
     * Returns the libraries used in the platform.
     *
     * @return the libraries used in the platform
     */
    public String[] getLibraries() {
        return libraries;
    }

    /**
     * Returns true if the platform is a placeholder.
     *
     * @return true if the platform is a placeholder
     */
    public boolean isPlaceholder() {
        return placeholder;
    }

    /**
     * Returns true the platforms match (os and flavor).
     *
     * @param other the platform to compare it to
     * @return true if the platforms match
     */
    public boolean matches(Platform other) {
        if (!osPrefix.equals(other.osPrefix)) {
            return false;
        }
        if (flavor.startsWith("cu") != other.flavor.startsWith("cu")) {
            return false;
        }
        return flavor.startsWith(other.flavor) || other.flavor.startsWith(flavor);
    }
}

/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package mobanisto.jpackage.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import static mobanisto.jpackage.internal.OverridableResource.createResource;

/**
 * Helper to install launchers as services using "systemd".
 */
public final class LinuxLaunchersAsServices extends UnixLaunchersAsServices {

    private LinuxLaunchersAsServices(PlatformPackage thePackage,
            Map<String, Object> params) throws IOException {
        super(thePackage, REQUIRED_PACKAGES, params, li -> {
            return new Launcher(thePackage, li.getName(), params);
        });
    }

    static ShellCustomAction create(PlatformPackage thePackage,
            Map<String, Object> params) throws IOException {
        if (StandardBundlerParam.isRuntimeInstaller(params)) {
            return ShellCustomAction.nop(REPLACEMENT_STRING_IDS);
        }
        return new LinuxLaunchersAsServices(thePackage, params);
    }

    public static Path getServiceUnitFileName(String packageName,
            String launcherName) {
        String baseName = launcherName.replaceAll("[\\s]", "_");
        return Path.of(packageName + "-" + baseName + ".service");
    }

    private static class Launcher extends UnixLauncherAsService {

        Launcher(PlatformPackage thePackage, String name,
                Map<String, Object> mainParams) {
            super(name, mainParams, createResource("unit-template.service",
                    mainParams).setCategory(I18N.getString(
                            "resource.systemd-unit-file")));

            unitFilename = getServiceUnitFileName(thePackage.name(), getName());

            getResource()
                    .setPublicName(unitFilename)
                    .addSubstitutionDataEntry("APPLICATION_LAUNCHER",
                            Enquoter.forPropertyValues().applyTo(
                                    thePackage.installedApplicationLayout().launchersDirectory().resolve(
                                            getName()).toString()));
        }

        @Override
        Path descriptorFilePath(Path root) {
            return root.resolve("lib/systemd/system").resolve(unitFilename);
        }

        private final Path unitFilename;
    }

    private final static List<String> REQUIRED_PACKAGES = List.of("systemd",
            "coreutils" /* /usr/bin/wc */, "grep");
}

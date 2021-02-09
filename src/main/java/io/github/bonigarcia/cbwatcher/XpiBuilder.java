/*
 * (C) Copyright 2021 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.cbwatcher;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;

/**
 * Cross Browser Watcher extension builder (XPI file).
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */
public class XpiBuilder {

    static final Logger log = getLogger(lookup().lookupClass());

    static final String SOURCE_FOLDER = "xpi";
    static final String TARGET_FOLDER = "target";
    static final String CBEXTENSION_NAME = "cbwatcher.xpi";

    public File build() throws FileNotFoundException, IOException {
        log.debug("Building Cross Browser Watcher (source folder: {})",
                SOURCE_FOLDER);
        File targetFile = new File(TARGET_FOLDER, CBEXTENSION_NAME);
        zipFolder(new File(SOURCE_FOLDER), targetFile);
        log.debug("Extension available on {}/{}", TARGET_FOLDER,
                CBEXTENSION_NAME);

        return targetFile;
    }

    private void zipFolder(File srcFolder, File destZipFile)
            throws FileNotFoundException, IOException {
        try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
                ZipOutputStream zip = new ZipOutputStream(fileWriter)) {
            addFolderToZip(srcFolder, srcFolder, zip);
        }
    }

    private void addFileToZip(File rootPath, File srcFile, ZipOutputStream zip)
            throws FileNotFoundException, IOException {
        if (srcFile.isDirectory()) {
            addFolderToZip(rootPath, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            try (FileInputStream in = new FileInputStream(srcFile)) {
                String name = srcFile.getPath();
                name = name.replace(rootPath.getPath(), "").substring(1);
                log.debug("Zipping {}", name);
                zip.putNextEntry(new ZipEntry(name));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    private void addFolderToZip(File rootPath, File srcFolder,
            ZipOutputStream zip) throws FileNotFoundException, IOException {
        for (File fileName : srcFolder.listFiles()) {
            addFileToZip(rootPath, fileName, zip);
        }
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        XpiBuilder xpiBuilder = new XpiBuilder();
        xpiBuilder.build();
    }

}
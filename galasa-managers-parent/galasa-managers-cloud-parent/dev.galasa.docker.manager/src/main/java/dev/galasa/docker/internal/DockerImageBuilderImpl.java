package dev.galasa.docker.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import dev.galasa.docker.DockerManagerException;

public class DockerImageBuilderImpl implements IDockerImageBuilder {
    DockerEngineImpl engine;

    public DockerImageBuilderImpl(DockerEngineImpl engine) {
        this.engine = engine;
    }

    @Override
    public void buildImage(String imageName, InputStream dockerfile, Map<String,InputStream> resources)
            throws DockerManagerException {
        try {
            engine.buildImage(imageName, createDockerTagGz(dockerfile, resources));
        } catch (IOException e) {
            throw new DockerManagerException("Failed to build image", e);
        }

    }

    /** Create a temp dir and record it
     * 
     * Create the 
     */
    private Path createDockerTagGz(InputStream dockerfile, Map<String,InputStream> buildResources) throws DockerManagerException {
        File buildDir = new File("/tmp/galasa-build-dir");
        Path outputTar = Paths.get(buildDir.getAbsolutePath() + "/Dockerfile.tar.gz");

        if (buildDir.exists()){
            buildDir.delete();
        }
        buildDir.mkdir();

        try {
            OutputStream fOut = Files.newOutputStream(outputTar);
            BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
            GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
            TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);

            // Create the dockerfile
            TarArchiveEntry dockerEntry = new TarArchiveEntry("Dockerfile");
            dockerEntry.setSize(dockerfile.available());
            tOut.putArchiveEntry(dockerEntry);
            IOUtils.copy(dockerfile, tOut);
            tOut.closeArchiveEntry();

            // Put the rest of the resources in a flat dir
            for (String fileName : buildResources.keySet()) {
                TarArchiveEntry entry = new TarArchiveEntry(fileName);
                entry.setSize(buildResources.get(fileName).available());
                tOut.putArchiveEntry(entry);
                IOUtils.copy(buildResources.get(fileName), tOut);
                tOut.closeArchiveEntry();
            }

            tOut.flush();
            tOut.close();

        } catch(IOException e) {
            throw new DockerManagerException("Failed to create Dockerfile.tar.gz", e);
        }
        return outputTar;
    }
}
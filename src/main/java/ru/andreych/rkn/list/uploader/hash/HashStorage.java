package ru.andreych.rkn.list.uploader.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HashStorage {
    private static final Logger LOG = LogManager.getLogger(HashStorage.class);
    private final Path hashFilePath;
    private final String hashFilePathString;

    public HashStorage(final String hashFilePath) {
        this.hashFilePathString = hashFilePath;
        this.hashFilePath = Paths.get(this.hashFilePathString);
    }

    public byte[] getHash() throws IOException {
        LOG.info("Getting hash from file {}.", this.hashFilePathString);
        if (!Files.exists(this.hashFilePath)) {
            LOG.info("There's no file with hash at {} path.", this.hashFilePathString);
            return new byte[0];
        }
        final byte[] hash = Files.readAllBytes(this.hashFilePath);
        LOG.info("Got hash from {}.", this.hashFilePathString);
        return hash;
    }

    public void storeHash(final byte[] hash) throws IOException {
        LOG.info("Storing hash to file {}.", this.hashFilePathString);
        Files.createDirectories(this.hashFilePath.getParent());
        Files.write(this.hashFilePath, hash);
        LOG.info("Hash has been stored to {}.", this.hashFilePathString);
    }
}

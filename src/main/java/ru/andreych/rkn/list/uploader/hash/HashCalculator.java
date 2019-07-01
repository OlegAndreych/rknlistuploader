package ru.andreych.rkn.list.uploader.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashCalculator {
    private static final Logger LOG = LogManager.getLogger(HashCalculator.class);

    public static byte[] calculate(final List<String> addressStrings) throws NoSuchAlgorithmException {
        LOG.info("Calculating addresses hash.");
        final MessageDigest digest = MessageDigest.getInstance("MD5");
        addressStrings.forEach(s -> digest.update(s.getBytes(StandardCharsets.UTF_8)));
        final byte[] hash = digest.digest();
        LOG.info("Addresses hash has been calculated.");
        return hash;
    }
}

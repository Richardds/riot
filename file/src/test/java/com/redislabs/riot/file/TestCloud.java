package com.redislabs.riot.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TestCloud extends AbstractFileTest {


    @Test
    public void importGcs() throws Exception {
        runFile("/cloud/import-gcs.txt");
        List<String> keys = commands().keys("beer:*");
        Assertions.assertEquals(4432, keys.size());
        Map<String, String> beer1 = commands().hgetall("beer:1");
        Assertions.assertEquals("Hocus Pocus", beer1.get("name"));
    }

    @Test
    public void importS3() throws Exception {
        runFile("/cloud/import-s3.txt");
        List<String> keys = commands().keys("beer:*");
        Assertions.assertEquals(4432, keys.size());
        Map<String, String> beer1 = commands().hgetall("beer:1");
        Assertions.assertEquals("Hocus Pocus", beer1.get("name"));
    }
}

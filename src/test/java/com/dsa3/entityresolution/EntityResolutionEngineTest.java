package com.dsa3.entityresolution;

import com.dsa3.entityresolution.algorithms.matching.EntityResolutionEngine;
import com.dsa3.entityresolution.algorithms.matching.EntityResolutionEngine.ResolutionResult;
import com.dsa3.entityresolution.data.CsvProductLoader;
import com.dsa3.entityresolution.data.SampleDataGenerator;
import com.dsa3.entityresolution.model.ProductListing;
import com.dsa3.entityresolution.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityResolutionEngineTest {

    @BeforeAll
    public static void setupSampleData() throws IOException {
        SampleDataGenerator.generateSampleDatasets(Constants.DEFAULT_SAMPLE_CSV, Constants.DEFAULT_LARGER_CSV);
    }

    @Test
    public void testFullEntityResolutionOnSampleCsv() throws IOException {
        List<ProductListing> listings = CsvProductLoader.loadFromCsv(Constants.DEFAULT_SAMPLE_CSV);
        assertFalse(listings.isEmpty());

        EntityResolutionEngine engine = new EntityResolutionEngine();
        ResolutionResult result = engine.resolveEntities(listings, true);

        assertNotNull(result);
        assertFalse(result.getCanonicalCatalog().isEmpty());
        assertTrue(result.getCanonicalCatalog().size() < listings.size()); // Groups were successfully formed
    }
}

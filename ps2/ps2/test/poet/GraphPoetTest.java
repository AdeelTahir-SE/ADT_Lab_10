/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {

    // Testing strategy
    //   - Constructor builds graph correctly from corpus
    //   - Poem preserves original input case
    //   - Inserts correct bridge words for two-edge paths with maximum weight
    //   - Handles no bridge words correctly
    //   - Edge cases: empty string, single word, repeated words, punctuation

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /** Helper to create a temporary corpus file */
    private File createTempCorpus(String content) throws IOException {
        File temp = File.createTempFile("corpus", ".txt");
        temp.deleteOnExit();
        try (FileWriter writer = new FileWriter(temp)) {
            writer.write(content);
        }
        return temp;
    }

    @Test
    public void testGraphPoemBasic() throws IOException {
        File corpus = createTempCorpus("This is a test of the Mugar Omni Theater sound system.");
        GraphPoet poet = new GraphPoet(corpus);

        String input = "Test the system.";
        String expected = "Test of the system.";
        String output = poet.poem(input);
        assertEquals(expected, output);
    }



    @Test
    public void testPoemNoBridgeWord() throws IOException {
        File corpus = createTempCorpus("hello world");
        GraphPoet poet = new GraphPoet(corpus);

        String input = "foo bar";
        String output = poet.poem(input);
        assertEquals("foo bar", output); // no bridge word possible
    }

    @Test
    public void testPoemEmptyInput() throws IOException {
        File corpus = createTempCorpus("any text here");
        GraphPoet poet = new GraphPoet(corpus);

        String output = poet.poem("");
        assertEquals("", output);
    }

    @Test
    public void testPoemSingleWord() throws IOException {
        File corpus = createTempCorpus("any text here");
        GraphPoet poet = new GraphPoet(corpus);

        String output = poet.poem("Hello");
        assertEquals("Hello", output);
    }


}

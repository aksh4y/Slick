package edu.northeastern.ccs.im;

import org.junit.jupiter.api.Test;

public class NextDoesNotExistExceptionTest {

    @Test
    public void testExceptionCreation() {
        NextDoesNotExistException exp = new NextDoesNotExistException("Error");
    }
}

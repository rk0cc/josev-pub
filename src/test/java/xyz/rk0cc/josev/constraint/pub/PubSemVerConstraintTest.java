package xyz.rk0cc.josev.constraint.pub;

import org.junit.jupiter.api.*;
import xyz.rk0cc.josev.NonStandardSemVerException;
import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.josev.constraint.SemVerConstraint;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
final class PubSemVerConstraintTest {
    @DisplayName("Test pub parser")
    @Order(1)
    @Test
    void testPubSemVerParse() {
        assertDoesNotThrow(() -> PubSemVerConstraint.parse("1.0.0"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse("^1.0.0"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse(">1.0.0 <2.0.0"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse(">=1.0.0 <=1.0.2"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse(">=1.0.0"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse("<=1.0.0"));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse(null));
        assertDoesNotThrow(() -> PubSemVerConstraint.parse("any"));
        assertThrows(
                IllegalArgumentException.class,
                () -> PubSemVerConstraint.parse(PubConstraintPattern.ANY, "^3.0.0")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> PubSemVerConstraint.parse(PubConstraintPattern.TRADITIONAL, "<3.0.1 >4.0.2")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> PubSemVerConstraint.parse(PubConstraintPattern.TRADITIONAL, "<=3.0.1 >2.0.2")
        );
    }

    @DisplayName("Test parsing from parent class")
    @Order(2)
    @Test
    void testParentParser() {
        assertDoesNotThrow(() -> SemVerConstraint.parse(PubSemVerConstraint.class, "^1.0.0"));
    }

    @DisplayName("Test pub version constraint pattern validation")
    @Order(3)
    @Test
    void testInRange() {
        final PubSemVerConstraint anyConstraint = PubSemVerConstraint.parse("any"),
                caretConstraint = PubSemVerConstraint.parse("^2.0.2"),
                absoluteConstraint = PubSemVerConstraint.parse("3.0.0"),
                traditionalConstraint = PubSemVerConstraint.parse(">=2.12.0 <3.0.0");

        try {
            assertTrue(anyConstraint.isInRange("99.99.99"));
            assertTrue(anyConstraint.isInRange("0.0.0"));
            assertTrue(anyConstraint.isInRange("0.0.0-alpha"));
            assertTrue(caretConstraint.isInRange("2.0.2"));
            assertTrue(caretConstraint.isInRange("2.0.2+1"));
            assertFalse(caretConstraint.isInRange("2.0.1-rc.1"));
            assertFalse(caretConstraint.isInRange("2.0.2-rc.2"));
            assertFalse(caretConstraint.isInRange("3.0.0"));
            assertFalse(caretConstraint.isInRange("3.0.0-beta"));
            assertTrue(caretConstraint.isInRange("2.99.0"));
            assertFalse(caretConstraint.isInRange("2.0.1"));
            assertFalse(absoluteConstraint.isInRange("3.0.0-rc.1"));
            assertFalse(absoluteConstraint.isInRange("3.0.0+1"));
            assertTrue(traditionalConstraint.isInRange("2.12.0"));
            assertFalse(traditionalConstraint.isInRange("3.0.0"));
            assertTrue(traditionalConstraint.isInRange("2.13.0-beta.1"));
        } catch (NonStandardSemVerException e) {
            fail(e);
        }
    }
}

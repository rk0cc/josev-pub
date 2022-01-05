package xyz.rk0cc.josev.constraint.pub;

import org.junit.jupiter.api.*;
import xyz.rk0cc.josev.constraint.SemVerConstraint;

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
}

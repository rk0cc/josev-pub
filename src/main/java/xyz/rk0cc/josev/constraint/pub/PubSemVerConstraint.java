package xyz.rk0cc.josev.constraint.pub;

import xyz.rk0cc.josev.*;
import xyz.rk0cc.josev.constraint.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A fully implemented {@link SemVerConstraint} for pub package (Dart/Flutter).
 *
 * @since 1.0.0
 *
 * @see PubConstraintPattern
 * @see <a href="https://dart.dev/tools/pub/dependencies#version-constraints">Version constraint for pub dependency</a>.
 */
@SemVerConstraintParser
public final class PubSemVerConstraint extends SemVerConstraint<PubConstraintPattern> {
    /**
     * Create new data of pub package dependency's version constraint.
     *
     * @param constraintPattern Pattern uses in this constraint
     * @param rawConstraint A {@link String} of user input, can be <code>null</code>
     * @param start A {@link SemVerRangeNode node} contains which minimum version can be allowed.
     * @param end A {@link SemVerRangeNode node} contains which maximum version can be allowed.
     */
    private PubSemVerConstraint(
            @Nonnull PubConstraintPattern constraintPattern,
            @Nullable String rawConstraint,
            @Nullable SemVerRangeNode start,
            @Nullable SemVerRangeNode end
    ) {
        super(constraintPattern, rawConstraint, start, end);
    }

    /**
     * Parse <code>versionConstraint</code> on pub package dependency.
     * <br/>
     * This method can be invoked by {@link SemVerConstraint#parse(Class, String)}.
     *
     * @param versionConstraint Version constraint {@link String} of pub package dependency.
     *
     * @return A {@link PubSemVerConstraint} of <code>versionConstraint</code>.
     */
    @Nonnull
    public static PubSemVerConstraint parse(@Nullable String versionConstraint) {
        List<PubConstraintPattern> constraintPatterns = Arrays.stream(PubConstraintPattern.values())
                .filter(pubcp -> pubcp.isValidConstraintMethods(versionConstraint))
                .toList();

        assert constraintPatterns.size() == 1;

        return parse(constraintPatterns.get(0), versionConstraint);
    }

    /**
     * Parse <code>versionConstraint</code> on pub package dependency and provides which {@link PubConstraintPattern} is
     * preferred.
     *
     * @param pattern Preferred {@link PubConstraintPattern} is used in {@link PubSemVerConstraint}.
     * @param versionConstraint Version constraint {@link String} of pub package dependency.
     *
     * @return A {@link PubSemVerConstraint} of <code>versionConstraint</code>.
     *
     * @throws IllegalArgumentException If provided {@link PubConstraintPattern} is invalid for
     *                                  <code>versionConstraint</code>.
     */
    @Nonnull
    public static PubSemVerConstraint parse(
            @Nonnull PubConstraintPattern pattern,
            @Nullable String versionConstraint
    ) {
        try {
            assert pattern.isValidConstraintMethods(versionConstraint);

            switch (pattern) {
                case ANY:
                    return new PubSemVerConstraint(pattern, versionConstraint, null, null);
                case CARET:
                    assert versionConstraint != null;

                    SemVer importedSV = SemVer.parse(versionConstraint.substring(1));

                    return new PubSemVerConstraint(
                            pattern,
                            versionConstraint,
                            new SemVerRangeNode(importedSV, '>', true),
                            new SemVerRangeNode(new SemVer(importedSV.major() + 1), '<', false)
                    );
                case TRADITIONAL:
                    assert versionConstraint != null;

                    final Function<String, SemVerRangeNode> vcsReader = s -> {
                        boolean oE = s.contains("=");
                        try {
                            return new SemVerRangeNode(
                                    SemVer.parse(s.substring(oE ? 2 : 1)),
                                    s.charAt(0),
                                    oE
                            );
                        } catch (NonStandardSemVerException e) {
                            throw new AssertionError(e);
                        }
                    };

                    final String[] constraintSection = versionConstraint.split("\\s");

                    switch (constraintSection.length) {
                        case 1:
                            SemVerRangeNode tvcNode = vcsReader.apply(constraintSection[0]);
                            return tvcNode.operator() == '>'
                                    ? new PubSemVerConstraint(pattern, versionConstraint, tvcNode, null)
                                    : new PubSemVerConstraint(pattern, versionConstraint, null, tvcNode);
                        case 2:
                            return new PubSemVerConstraint(
                                    pattern,
                                    versionConstraint,
                                    vcsReader.apply(constraintSection[0]),
                                    vcsReader.apply(constraintSection[1])
                            );
                        default:
                            throw new AssertionError("Found too much section in traditional syntax");
                    }
                case ABSOLUTE:
                    assert versionConstraint != null;
                    SemVer absVer = SemVer.parse(versionConstraint);
                    return new PubSemVerConstraint(
                            pattern,
                            versionConstraint,
                            new SemVerRangeNode(absVer, '>', true),
                            new SemVerRangeNode(absVer, '<', true)
                    );
                default:
                    throw new IllegalArgumentException("Pattern " + pattern + " is undefined.");
            }
        } catch (NonStandardSemVerException | AssertionError e) {
            throw new IllegalArgumentException(e);
        }
    }
}

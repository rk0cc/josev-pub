package xyz.rk0cc.josev.constraint.pub;

import xyz.rk0cc.josev.*;
import xyz.rk0cc.josev.constraint.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SemVerConstraintParser
public final class PubSemVerConstraint extends SemVerConstraint<PubConstraintPattern> {
    private PubSemVerConstraint(
            @Nonnull PubConstraintPattern constraintPattern,
            @Nullable String rawConstraint,
            @Nullable SemVerRangeNode start,
            @Nullable SemVerRangeNode end
    ) {
        super(constraintPattern, rawConstraint, start, end);
    }

    @Nonnull
    public static PubSemVerConstraint parse(@Nullable String versionConstraint) {
        List<PubConstraintPattern> constraintPatterns = Arrays.stream(PubConstraintPattern.values())
                .filter(pubcp -> pubcp.isValidConstraintMethods(versionConstraint))
                .toList();

        assert constraintPatterns.size() == 1;

        final PubConstraintPattern pattern = constraintPatterns.get(0);

        try {
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
        } catch (NonStandardSemVerException e) {
            throw new AssertionError(e);
        }
    }
}

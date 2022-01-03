package xyz.rk0cc.josev.constraint.pub;

import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.josev.constraint.ConstraintPattern;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public enum PubConstraintPattern implements ConstraintPattern<PubConstraintPattern> {
    ANY(Pattern.compile("^any$"), true),
    CARET(Pattern.compile("^\\^" + SemVer.SEMVER_REGEX + "$"), false),
    TRADITIONAL(Pattern.compile("^("
            + "(>=?" + SemVer.SEMVER_REGEX + ")"
            + "|(<=?" + SemVer.SEMVER_REGEX + ")"
            + "|(>=?" + SemVer.SEMVER_REGEX + "\\s<=?" + SemVer.SEMVER_REGEX + ")"
            + ")$"), false),
    ABSOLUTE(Pattern.compile("^" + SemVer.SEMVER_REGEX + "$"), false);

    private final Pattern constraintPattern;
    private final boolean acceptParseNull;

    PubConstraintPattern(@Nonnull Pattern constraintPattern, boolean acceptParseNull) {
        this.constraintPattern = constraintPattern;
        this.acceptParseNull = acceptParseNull;
    }

    @Nonnull
    @Override
    public Pattern constraintPattern() {
        return constraintPattern;
    }

    @Override
    public boolean acceptParseNull() {
        return acceptParseNull;
    }
}

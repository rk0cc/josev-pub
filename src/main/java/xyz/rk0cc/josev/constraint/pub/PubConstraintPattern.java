package xyz.rk0cc.josev.constraint.pub;

import xyz.rk0cc.josev.SemVer;
import xyz.rk0cc.josev.constraint.ConstraintPattern;

import javax.annotation.Nonnull;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * An {@link Enum} of {@link ConstraintPattern} for {@link PubSemVerConstraint version constraint in pub dependnecy}.
 *
 * @since 1.0.0
 *
 * @see <a href="https://dart.dev/tools/pub/dependencies#version-constraints">Version constraint for pub dependency</a>.
 */
public enum PubConstraintPattern implements ConstraintPattern<PubConstraintPattern> {
    /**
     * Any version can be imported by applying "<code>any</code>" or <code>null</code>.
     * <br/>
     * Apply this pattern to {@link PubSemVerConstraint#isInRange(SemVer)} will always return <code>true</code>.
     */
    ANY(Pattern.compile("^any$"), true),
    /**
     * Start import the specific {@link SemVer version} until latest {@link SemVer#minor() minor release} in the same
     * {@link SemVer#major() major version}.
     * <br/>
     * This constraint <b>can not</b> uses <code>SDK constraint</code> in <code>environment</code> field of
     * <code>pubspec.yaml</code>.
     */
    CARET(Pattern.compile("^\\^" + SemVer.SEMVER_REGEX + "$"), false),
    /**
     * Traditional syntax of version constraint which provided either or both minimum bound and maximum bound of the
     * {@link SemVer version} that can be accepted.
     */
    TRADITIONAL(Pattern.compile("^("
            + "(>=?" + SemVer.SEMVER_REGEX + ")"
            + "|(<=?" + SemVer.SEMVER_REGEX + ")"
            + "|(>=?" + SemVer.SEMVER_REGEX + "\\s<=?" + SemVer.SEMVER_REGEX + ")"
            + ")$"), false),
    /**
     * Uses specific {@link SemVer version} of the package dependency, any newer or older {@link SemVer version} will
     * be rejected.
     */
    ABSOLUTE(Pattern.compile("^" + SemVer.SEMVER_REGEX + "$"), false);

    /**
     * {@link Pattern} for validating a {@link String} of {@link SemVer}.
     */
    private final Pattern constraintPattern;
    /**
     * Allowing to parse {@link SemVer} as <code>null</code>.
     */
    private final boolean acceptParseNull;

    /**
     * New {@link SemVer}'s constraint pattern for {@link PubSemVerConstraint}.
     *
     * @param constraintPattern {@link Pattern} for validating a {@link String} of {@link SemVer}.
     * @param acceptParseNull Allowing to parse {@link SemVer} as <code>null</code>.
     */
    PubConstraintPattern(@Nonnull Pattern constraintPattern, boolean acceptParseNull) {
        this.constraintPattern = constraintPattern;
        this.acceptParseNull = acceptParseNull;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Predicate<String> conditionFunction() {
        return s -> constraintPattern.matcher(s).matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean acceptParseNull() {
        return acceptParseNull;
    }
}

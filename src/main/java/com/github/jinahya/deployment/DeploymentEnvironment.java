package com.github.jinahya.deployment;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Constants for deployment environment.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://en.wikipedia.org/wiki/Deployment_environment">Deployment environment (Wikipedia)</a>
 */
public enum DeploymentEnvironment {

    // -----------------------------------------------------------------------------------------------------------------
    LOCAL(),

    DEVELOPMENT("trunk"),

    INTEGRATION(),

    TEST("qa", "internal-acceptance"),

    STAGING("stage", "pre-production", "external-client-acceptance"),

    PRODUCTION("live");

    // -----------------------------------------------------------------------------------------------------------------
    private static final String REGEXP = "[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*";

    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    private static String validates(final String alias) {
        if (alias == null) {
            throw new NullPointerException("alias is null");
        }
        if (!PATTERN.matcher(alias).matches()) {
            throw new IllegalArgumentException("alias('" + alias + "') doesn't match to " + REGEXP);
        }
        return alias;
    }

    private static boolean matches(final String alias, final String candidate) {
        if (candidate == null) {
            throw new NullPointerException("candidate is null");
        }
        if (alias.equalsIgnoreCase(candidate)) {
            return true;
        }
        if (alias.replaceAll("-", "").equalsIgnoreCase(candidate)) {
            return true;
        }
        return false;
    }

    public static DeploymentEnvironment of(final String alias) {
        if (alias == null) {
            throw new NullPointerException("alias is null");
        }
        try {
            return valueOf(alias.toUpperCase());
        } catch (final IllegalArgumentException iae) {
        }
        for (final DeploymentEnvironment v : values()) {
            for (final String a : v.aliases) {
                if (matches(a, alias)) {
                    return v;
                }
            }
        }
        throw new IllegalArgumentException("no constant matches for '" + alias + "'");
    }

    public static boolean alias(final DeploymentEnvironment value, final String alias) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        validates(alias);
        for (final DeploymentEnvironment v : values()) {
            if (v == value) {
                continue;
            }
            for (final String a : v.aliases) {
                if (matches(a, alias)) {
                    throw new IllegalStateException("alias('" + alias + "') is already mapped to " + v);
                }
            }
        }
        return value.aliases.add(alias);
    }

    // -----------------------------------------------------------------------------------------------------------------
    DeploymentEnvironment(final String... aliases) {
        this.aliases = new HashSet<String>();
        for (final String alias : aliases) {
            this.aliases.add(validates(alias));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    public boolean alias(final String alias) {
        return alias(this, alias);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Set<String> aliases;
}

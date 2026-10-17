/*
 * Copyright (c) 2023. JEFF Media GbR / mfnalex et al.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jeff_media.jefflib.data;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Replacement for JeffLib's version parser, which cannot handle versions whose
 * patch segment is not numeric (e.g. "26.3.build.202-stable").
 */
public class McVersion implements Comparable<McVersion> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?");
    private static final McVersion CURRENT_VERSION = parse(Bukkit.getBukkitVersion());
    private static final McVersion v1_17 = new McVersion(1, 17);

    private final int major;
    private final int minor;
    private final int patch;

    public McVersion(final int major, final int minor) {
        this(major, minor, 0);
    }

    public McVersion(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    private static McVersion parse(final String version) {
        final Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.find()) {
            return new McVersion(0, 0);
        }
        return new McVersion(number(matcher.group(1)), number(matcher.group(2)), number(matcher.group(3)));
    }

    private static int number(final String value) {
        return value == null ? 0 : Integer.parseInt(value);
    }

    public static McVersion current() {
        return CURRENT_VERSION;
    }

    public boolean hasVersionInNmsPackageName() {
        return !this.isAtLeast(v1_17);
    }

    public boolean isAtLeast(final McVersion other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    public int compareTo(@NotNull final McVersion other) {
        if (this.major > other.major) return 3;
        if (other.major > this.major) return -3;
        if (this.minor > other.minor) return 2;
        if (other.minor > this.minor) return -2;
        return Integer.compare(this.patch, other.patch);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final McVersion mcVersion = (McVersion) o;
        return major == mcVersion.major && minor == mcVersion.minor && patch == mcVersion.patch;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        if (patch == 0) {
            return major + "." + minor;
        } else {
            return major + "." + minor + "." + patch;
        }
    }

    public boolean isAtLeast(final int major, final int minor, final int patch) {
        return this.isAtLeast(new McVersion(major, minor, patch));
    }

    public boolean isAtLeast(final int major, final int minor) {
        return this.isAtLeast(new McVersion(major, minor));
    }

}

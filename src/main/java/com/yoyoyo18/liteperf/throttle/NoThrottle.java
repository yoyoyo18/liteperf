package com.yoyoyo18.liteperf.throttle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this annotation to entity classes or block entity classes to opt out of LitePerf throttling.
 * Other mods may choose to honor this marker when interacting with LitePerf; the annotation is
 * purely informational unless the other mod checks for it.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoThrottle {
}

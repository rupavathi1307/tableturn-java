package com.tableturn.table;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * Seeds the floor once every bean exists and before Tomcat starts listening, so no request can see a half-loaded
 * floor. (An ApplicationRunner would run after the server is already accepting requests.)
 */
@Component
public class FloorSeeder implements SmartInitializingSingleton {

    private final SeedService seed;

    public FloorSeeder(SeedService seed) {
        this.seed = seed;
    }

    @Override
    public void afterSingletonsInstantiated() {
        seed.seed();
    }
}
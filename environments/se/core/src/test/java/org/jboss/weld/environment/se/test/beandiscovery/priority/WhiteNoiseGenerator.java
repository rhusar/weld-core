package org.jboss.weld.environment.se.test.beandiscovery.priority;

import jakarta.enterprise.context.Dependent;

@Dependent
public class WhiteNoiseGenerator implements SoundSource {

    @Override
    public String generateSound() {
        return "ssssssssssss";
    }

}

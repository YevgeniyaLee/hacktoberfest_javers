package org.javers.shadow;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final CdoSnapshot cdoSnapshot;
    private Object shadow;
    private Set<Wiring> wirings = new HashSet<>();

    ShadowBuilder(CdoSnapshot cdoSnapshot, Object shadow) {
        this.cdoSnapshot = cdoSnapshot;
        this.shadow = shadow;
    }

    void withStub(Object shadowStub) {
        this.shadow = shadowStub;
    }

    Object getShadow() {
        return shadow;
    }

    /**
     * nullable
     */
    CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

    void addReferenceWiring(JaversProperty property, ShadowBuilder targetShadow) {
        this.wirings.add(new ReferenceWiring(property, targetShadow));
    }

    void addEnumerableWiring(JaversProperty property, Object targetWithShadows) {
        this.wirings.add(new EnumerableWiring(property, targetWithShadows));
    }

    void wire() {
        Set<Wiring> copy = new HashSet<>();
        wirings.forEach(Wiring::wire);
        for(Wiring wiring : wirings){
            Wiring copiedWiring = wiring.wire();
            copy.add(copiedWiring);
        }
        this.wirings = copy;
    }

    private abstract class Wiring {
        final JaversProperty property;

        Wiring(JaversProperty property) {
            this.property = property;
        }

        abstract Wiring wire();
    }

    private class ReferenceWiring extends Wiring {
        final ShadowBuilder target;

        ReferenceWiring(JaversProperty property, ShadowBuilder targetShadow) {
            super(property);
            this.target = targetShadow;
        }

        @Override
        Wiring wire() {
            Property newProperty = property.set(shadow, target.shadow);
            return new ReferenceWiring((JaversProperty) newProperty, this.target);
        }
    }

    private class EnumerableWiring extends Wiring {
        final Object targetWithShadows;

        EnumerableWiring(JaversProperty property, Object targetWithShadows) {
            super(property);
            this.targetWithShadows = targetWithShadows;
        }

        @Override
        Wiring wire() {
            EnumerableType propertyType = property.getType();

            Object targetContainer = propertyType.map(targetWithShadows, (valueOrShadow) -> {
                if (valueOrShadow instanceof ShadowBuilder) {
                    //injecting reference to shadow
                    return ((ShadowBuilder) valueOrShadow).shadow;
                }
                return valueOrShadow; //vale is passed as is
            });

            Property newProperty = property.set(shadow, targetContainer);
            return new EnumerableWiring((JaversProperty) newProperty, this.targetWithShadows);
        }
    }
}

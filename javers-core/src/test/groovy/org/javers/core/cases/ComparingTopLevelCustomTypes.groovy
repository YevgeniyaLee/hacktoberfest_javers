package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.CustomType
import spock.lang.Specification

class ComparingTopLevelCustomTypes extends Specification {

    @Entity
    class Ent {
        String value

        Ent(String value) {
            this.value = value
        }
    }

    class FunnyStringComparator implements CustomPropertyComparator<Ent, SetChange> {

        @Override
        public Optional<SetChange> compare(Ent left, Ent right, PropertyChangeMetadata metadata,
                                           Property property) {
            return [new SetChange(metadata),[new ValueAdded(right.value)]]
        }

        @Override
        public boolean equals(Ent a, Ent b) {
            return false;
        }

        @Override
        String toString(Ent value) {
            return null
        }
    }

    def "should compare top level CustomTypes" () {
        given:
            Javers javers = JaversBuilder
                .javers()
                .registerCustomComparator(new FunnyStringComparator(), Ent).build()

        when:
            println(javers.getTypeMapping(Ent))

            Diff diff = javers.compare(new Ent("aaa"), new Ent("aaa"))
            println(diff.sprintf())

        then:
            javers.getTypeMapping(Ent) instanceof CustomType
            diff.changes.size() == 1
            diff.changes[0] instanceof SetChange
    }
}
package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityFactory;
import org.javers.test.assertion.Assertions;
import org.junit.Test;

/**
 * @author bartosz walacik
 */
public abstract class ObjectWrapperTest {

    protected EntityFactory entityFactory;

    @Test
    public void shouldHoldEntityReference() {
        //given
        DummyUser cdo = new DummyUser("kaz");
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.getEntity()).isSameAs(entity);
    }

    @Test
    public void shouldHoldCdoReference() {
        //given
        DummyUser cdo = new DummyUser("kaz");
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.unwrapCdo()).isSameAs(cdo);
    }


    @Test
    public void shouldReturnCdoId() {
        //given
        DummyUser cdo = new DummyUser("Mad Kaz");
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.getCdoId()).isEqualTo("Mad Kaz");
    }
}
package org.javers.common.reflection;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.javers.common.string.ToStringBuilder.typeName;

/**
 * @author bartosz walacik
 */
public class JaversField extends JaversMember<Field> {

    protected JaversField(Field rawField, Type resolvedReturnType) {
        super(rawField, resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericType();
    }

    @Override
    public Class<?> getRawType() {
        return getRawMember().getType();
    }

    @Override
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().get(onObject);
        } catch (IllegalArgumentException ie) {
            return getOnMissingProperty(onObject);
        } catch (IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public JaversMember setEvenIfPrivate(Object onObject, Object value) { // todo
        try {

//            for (Field field : onObject.getClass().getDeclaredFields()) {
//                field.setAccessible(true);
//                if (value instanceof Collection && field.getName().equals("set")) {
//                    field.set(onObject, value);
//                }
//            }
            getRawMember().set(onObject, value);
            return new JaversField(this.getRawMember(), this.getGenericResolvedType());
        } catch (IllegalArgumentException ie) {
            String valueType = value == null ? "null" : value.getClass().getName();
            throw new JaversException(JaversExceptionCode.PROPERTY_SETTING_ERROR, valueType, this, ie.getClass().getName() + " - " + ie.getMessage());
        } catch (IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public String memberType() {
        return "Field";
    }
}

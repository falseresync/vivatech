package dev.falseresync.vivatech.api.delegate;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public interface DelegatedGenericProperty<T> extends DelegatedProperty {
    T get();

    void set(T value);

    @Override
    @ApiStatus.OverrideOnly
    int getAsInt();

    @Override
    @ApiStatus.OverrideOnly
    void setIntValue(int value);

    static <T> DelegatedGenericProperty<T> of(int index, T initialValue, ToIntFunction<T> toInt, IntFunction<T> fromInt) {
        Preconditions.checkArgument(index >= 0, "Delegated property indices must be non-negative");
        return new DelegatedGenericProperty<>() {
            private T value = initialValue;

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public T get() {
                return value;
            }

            @Override
            public void set(T value) {
                this.value = value;
            }

            @Override
            public int getAsInt() {
                return toInt.applyAsInt(value);
            }

            @Override
            public void setIntValue(int value) {
                this.value = fromInt.apply(value);
            }
        };
    }
}

package dev.falseresync.vivatech.api.delegate;

import com.google.common.base.Preconditions;

public interface DelegatedIntProperty extends DelegatedProperty {
    int get();

    default int increment() {
        return increment(1);
    }

    int increment(int value);

    default int decrement() {
        return decrement(1);
    }

    int decrement(int value);

    void set(int value);

    @Override
    @Deprecated
    default int getAsInt() {
        return get();
    }

    @Override
    @Deprecated
    default void setIntValue(int value) {
        set(value);
    }

    static DelegatedIntProperty of(int index, int initialValue) {
        Preconditions.checkArgument(index >= 0, "Delegated property indices must be non-negative");
        return new DelegatedIntProperty() {
            private int value = initialValue;

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public int get() {
                return value;
            }

            @Override
            public int increment(int value) {
                this.value += value;
                return this.value;
            }

            @Override
            public int decrement(int value) {
                this.value -= value;
                return this.value;
            }

            @Override
            public void set(int value) {
                this.value = value;
            }
        };
    }
}

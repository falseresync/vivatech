package dev.falseresync.vivatech.api.delegate;

import com.google.common.collect.Ordering;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import org.spongepowered.include.com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class PropertyDelegateBuilder {
    public static final PropertyDelegate EMPTY = new ArrayPropertyDelegate(0);
    protected final Set<DelegatedProperty> properties = new HashSet<>();

    public void track(DelegatedProperty property) {
        properties.add(property);
    }

    public PropertyDelegate build() {
        if (properties.isEmpty()) {
            return EMPTY;
        }
        final var size = properties.size();
        final var sorted = Ordering
                .from(Comparator.comparingInt(DelegatedProperty::getIndex))
                .immutableSortedCopy(properties);
        Preconditions.checkState(sorted.getLast().getIndex() != size - 1, "Indices of delegated properties must be strictly consecutive");
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return sorted.get(index).getAsInt();
            }

            @Override
            public void set(int index, int value) {
                sorted.get(index).setIntValue(value);
            }

            @Override
            public int size() {
                return size;
            }
        };
    }
}

package falseresync.vivatech.api.lifessence;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public interface LifessenceStorage {
    long getAmount();

    long getCapacity();

    boolean supportsInsertion();

    boolean supportsExtraction();

    long insert(long amount, TransactionContext transaction);

    long extract(long amount, TransactionContext transaction);
}

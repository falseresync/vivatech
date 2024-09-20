package falseresync.vivatech.api.lifessence.base;

import falseresync.vivatech.api.lifessence.LifessenceStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public abstract class SimpleLifessenceStorage extends SnapshotParticipant<Long> implements LifessenceStorage {
    protected final long capacity;
    protected long amount;

    protected SimpleLifessenceStorage(long capacity, long amount) {
        this.capacity = capacity;
        this.amount = amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long insert(long toInsert, TransactionContext transaction) {
        if (!supportsInsertion()) return 0;
        StoragePreconditions.notNegative(toInsert);

        var inserted = Math.min(toInsert, getCapacity() - getAmount());
        if (inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;

            return inserted;
        }

        return 0;
    }

    @Override
    public long extract(long toExtract, TransactionContext transaction) {
        if (!supportsExtraction()) return 0;
        StoragePreconditions.notNegative(toExtract);

        var extracted = Math.min(toExtract, getAmount());
        if (extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;

            return extracted;
        }

        return 0;
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.amount = snapshot;
    }
}

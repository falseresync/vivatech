package falseresync.lib.logging;

import org.slf4j.Logger;

public class BetterLogger {
    private final Logger delegate;
    private final String prefix;

    public BetterLogger(Logger logger, String prefix) {
        this.delegate = logger;
        this.prefix = "[%s]".formatted(prefix);
    }

    public Logger getDelegate() {
        return delegate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void trace(Object msg) {
        delegate.trace("{} {}", prefix, msg);
    }

    public void debug(Object msg) {
        delegate.debug("{} {}", prefix, msg);
    }

    public void info(Object msg) {
        delegate.info("{} {}", prefix, msg);
    }

    public void warn(Object msg) {
        delegate.warn("{} {}", prefix, msg);
    }

    public void error(Object msg) {
        delegate.error("{} {}", prefix, msg);
    }
}

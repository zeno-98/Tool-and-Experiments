package verification.frame;

import verification.uppaal.verify.Result;

import java.util.Set;

public interface Checker<T> {
    Result isSatisfied(T t, Set<String> downSet);
}

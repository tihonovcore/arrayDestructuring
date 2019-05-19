package arrayDestructuring;

import arrayDestructuring.ConvertFunctionVisitor.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about {@link jdk.nashorn.internal.ir.Block}
 */
class BlockDefinition {
    /**
     * {@link StringBuilder} with text representation of arrays definition
     */
    StringBuilder arrayDefinition = new StringBuilder();

    /**
     * {@link StringBuilder} with text representation of block
     */
    StringBuilder text = new StringBuilder();

    /**
     * {@code key} is array's name, gives {@link List} of variables,
     * which initialized by that array
     */
    Map<String, List<Pair>> variables = new HashMap<>();
}

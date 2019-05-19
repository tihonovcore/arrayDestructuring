package arrayDestructuring;

import arrayDestructuring.ConvertFunctionVisitor.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BlockDefinition {
    boolean beforeFirstDefinition = true;
    StringBuilder before = new StringBuilder();
    StringBuilder after = new StringBuilder();
    Map<String, List<Pair>> variables = new HashMap<>();
}

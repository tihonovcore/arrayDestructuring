package arrayDestructuring;

import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.parser.*;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

import java.io.File;
import java.io.IOException;

/**
 * todo
 */
public class Converter {
    public static void main(String[] args) throws IOException {
        System.out.print(convert(args[0]));
    }

    public static StringBuilder convert(String path) throws IOException {
        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("parse.only", true);
        options.set("scripting", true);

        ErrorManager errors = new ErrorManager();
        Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        Source source = Source.sourceFor("test", new File(path));

        Parser parser = new Parser(context.getEnv(), source, errors);
        FunctionNode functionNode = parser.parse();

        ConvertFunctionVisitor CFV = new ConvertFunctionVisitor(new LexicalContext());
        functionNode.accept(CFV);

        return  CFV.getString();
    }
}

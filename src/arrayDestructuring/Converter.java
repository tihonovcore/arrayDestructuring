package arrayDestructuring;

import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.parser.*;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Find variable initialisation by array and transform them.
 * <p>
 * Example:
 * var variable1 = array[0]
 * var variable2 = array[1];
 * Transforms to:
 * var [variable1, variable2] = array;
 */
public class Converter {
    /**
     * Run {@code convert(String path)} and save result as [sourceName].out.js
     *
     * @param args args[0] should contains filename for converting
     *             cannot be null, should have only one not-null element
     * @throws ConvertException throws if something wrong.
     *                          <ul>
     *                          <li>{@code args} == null</li>
     *                          <li>{@code args.length} != 1</li>
     *                          <li>{@code args[0]} == null</li>
     *                          <li>error while reading</li>
     *                          <li>error while writing</li>
     *                          <li>error while parsing</li>
     *                          </ul>
     */
    public static void main(String[] args) throws ConvertException {
        if (args == null || args.length != 1 || args[0] == null) {
            throw new ConvertException("Something wrong with String[] args");
        }

        String output = args[0].replaceAll(".js", ".out.js");
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(output))) {
            writer.write(convert(args[0]).toString());
        } catch (IOException | ConvertException e) {
            throw new ConvertException(e.getMessage());
        }
    }

    /**
     * Reads file by {@code path}, find variable initialisation through array,
     * and transform them uses <i>destructuring assignment</i>
     *
     * @param path path to source file
     * @return converted code
     * @throws ConvertException is reading or parsing error happened
     */
    public static StringBuilder convert(String path) throws ConvertException {
        Options options = new Options("nashorn");
        ErrorManager errors = new ErrorManager(new PrintWriter(OutputStream.nullOutputStream()));
        Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());

        Source source;
        try {
            source = Source.sourceFor("test", new File(path));
        } catch (IOException e) {
            throw new ConvertException("Error while reading: " + e.getMessage());
        }

        Parser parser = new Parser(context.getEnv(), source, errors);
        FunctionNode functionNode = parser.parse();

        if (errors.hasErrors()) {
            throw new ConvertException("Error while parsing.");
        }

        ConvertFunctionVisitor CFV = new ConvertFunctionVisitor(new LexicalContext());
        functionNode.accept(CFV);

        return CFV.getString();
    }
}

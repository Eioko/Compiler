package utils;

import frontend.lexer.Token;
import error.SysyError;
import midend.symbol.SymbolTableManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Set;

import static error.ErrorManager.errors;

public class FileProcess {
    private static final String inputFile = "testfile.txt";
    private static final String tokenOutputFile = "lexer.txt";
    private static final String errorFile = "error.txt";
    private static final String parserOutputFile = "parser.txt";
    private static final String symbolOutputFile = "symbol.txt";

    private static final Path inputPath = Path.of(inputFile);
    private static final Path tokenOutputPath = Path.of(tokenOutputFile);
    private static final Path errorPath = Path.of(errorFile);
    private static final Path parserOutputPath = Path.of(parserOutputFile);
    private static final Path symbolOutputPath = Path.of(symbolOutputFile);

    private static BufferedWriter tokenWriter;
    private static BufferedReader reader;
    private static BufferedWriter errorWriter;
    private static BufferedWriter parserWriter;
    private static BufferedWriter symbolWriter;

    public static boolean printParser = true;
    private FileProcess() {}

    private static void ensureInputExistsAndReadable() {
        if (!Files.exists(inputPath)) {
            throw new RuntimeException(new NoSuchFileException("输入文件不存在: " + inputPath.toAbsolutePath()));
        }
        if (!Files.isReadable(inputPath)) {
            throw new RuntimeException("没有读取权限: " + inputPath.toAbsolutePath());
        }
    }

    public static void initOutput() {
        try{
            tokenWriter = Files.newBufferedWriter(
                    tokenOutputPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            errorWriter = Files.newBufferedWriter(
                    errorPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            parserWriter = Files.newBufferedWriter(
                    parserOutputPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            symbolWriter = Files.newBufferedWriter(
                    symbolOutputPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e) {
            throw new RuntimeException("初始化输出错误");
        }
    }
    public static String readFile(){
        ensureInputExistsAndReadable();
        try {
            return Files.readString(inputPath, StandardCharsets.UTF_8);
        } catch (MalformedInputException mie) {
            // UTF-8 失败，尝试 GB18030
            try {
                byte[] raw = Files.readAllBytes(inputPath);
                String gb18030 = new String(raw, Charset.forName("GB18030"));
                return gb18030;
            } catch (Exception inner) {
                throw new RuntimeException(
                        "文件不是 UTF-8，且使用 GB18030 仍无法正确解码，请将 " +
                                inputPath.toAbsolutePath() + " 转换为 UTF-8 后重试。", inner);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("读取失败: " + inputPath.toAbsolutePath(), ioe);
        }
    }
    private static final ArrayList<String> tokenAndGrammarBuffer = new ArrayList<>();
    private static final Set<String> SUPPRESS = Set.of("Decl", "BlockItem", "BType");

    //token输出
    public static void bufferToken(Token t) {
        if(printParser){
            tokenAndGrammarBuffer.add(t.getTokenType() + " " + t.getTokenContent());
        }

    }
    public static void setPrintParserOn() {
        printParser = true;
    }
    public static void setPrintParserOff() {
        printParser = false;
    }

    // 非终结符结束输出
    public static <T> T finish(String name, T node) {
        if(printParser){
            if (!SUPPRESS.contains(name)) {
                tokenAndGrammarBuffer.add("<" + name + ">");
            }
        }
        return node;
    }
    //输出决策
    public static void flushAll() {
        try {
            if (errors == null || errors.isEmpty()) {
                for (String line : tokenAndGrammarBuffer) {
                    parserWriter.write(line);
                    parserWriter.newLine();
                }
                parserWriter.flush();

                symbolWriter.write(SymbolTableManager.getSymbolPrints());
                symbolWriter.flush();
            } else {
                // 有错误
                java.util.List<SysyError> sorted = new ArrayList<>(errors);
                sorted.sort(java.util.Comparator.comparingInt(SysyError::getLineNum));
                for (SysyError err : sorted) {
                    errorWriter.write(err.toString());
                    errorWriter.newLine();
                }
                errorWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("写出文件失败", e);
        } finally {
            tokenAndGrammarBuffer.clear();
        }
    }

    public static void printTokens(ArrayList<Token> tokens){
        for(Token token : tokens){
            try{
                tokenWriter.write(token.getTokenType()+" "+token.getTokenContent());
                tokenWriter.newLine();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        flushQuietly(tokenWriter);
    }
    public static void closeAll() {
        closeQuietly(tokenWriter);
        closeQuietly(errorWriter);
        closeQuietly(reader);
        closeQuietly(parserWriter);
        closeQuietly(symbolWriter);
    }
    private static void flushQuietly(BufferedWriter w) {
        if (w != null) {
            try { w.flush(); } catch (IOException ignored) {}
        }
    }
    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try { c.close(); } catch (IOException ignored) {}
        }
    }
}

package utils;

import frontend.lexer.Token;
import error.SysyError;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileProcess {
    private static final String inputFile = "testfile.txt";
    private static final String tokenOutputFile = "lexer.txt";
    private static final String errorFile = "error.txt";
    private static final String parserOutputFile = "parser.txt";

    private static final Path inputPath = Path.of(inputFile);
    private static final Path tokenOutputPath = Path.of(tokenOutputFile);
    private static final Path errorPath = Path.of(errorFile);
    private static final Path parserOutputPath = Path.of(parserOutputFile);

    private static BufferedWriter tokenWriter;
    private static BufferedReader reader;
    private static BufferedWriter errorWriter;
    private static BufferedWriter parserWriter;

    private FileProcess() {}

    private void initInput() throws IOException {
        if (!Files.exists(inputPath)) {
            throw new NoSuchFileException("输入文件不存在: " + inputPath.toAbsolutePath());
        }
        if (!Files.isReadable(inputPath)) {
            throw new IOException("没有读取权限: " +inputPath.toAbsolutePath());
        }
        reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
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
        }catch (IOException e) {
            throw new RuntimeException("初始化输出错误");
        }
    }
    public static String readFile(){
        try{
            return Files.readString(inputPath);
        }catch(Exception e){
            throw new RuntimeException("读取 testfile.txt 失败: " + inputPath.toAbsolutePath(), e);
        }
    }
    private static final ArrayList<String> tokenAndGrammarBuffer = new ArrayList<>();
    private static final Set<String> SUPPRESS = Set.of("Decl", "BlockItem", "BType");

    //token输出
    public static void bufferToken(Token t) {
        tokenAndGrammarBuffer.add(t.getTokenType() + " " + t.getTokenContent());
    }
    // 非终结符结束输出
    public static <T> T finish(String name, T node) {
        if (!SUPPRESS.contains(name)) {
            tokenAndGrammarBuffer.add("<" + name + ">");
        }
        return node;
    }
    //输出决策
    public static void flushAll(List<SysyError> errors) {
        try {
            if (errors == null || errors.isEmpty()) {
                for (String line : tokenAndGrammarBuffer) {
                    parserWriter.write(line);
                    parserWriter.newLine();
                }
                parserWriter.flush();
            } else {
                // 有错误
                java.util.List<SysyError> sorted = new ArrayList<>(errors);
                sorted.sort(java.util.Comparator.comparingInt(SysyError::getLineNum));
                for (SysyError err : errors) {
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
    public static void printErrors(ArrayList<SysyError> errors){
        for(SysyError error : errors){
            try{
                errorWriter.write(error.toString());
                errorWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        flushQuietly(errorWriter);
    }
    public static void closeAll() {
        closeQuietly(tokenWriter);
        closeQuietly(errorWriter);
        closeQuietly(reader);
        closeQuietly(parserWriter);
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

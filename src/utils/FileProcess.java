package utils;

import frontend.lexer.Token;

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

public class FileProcess {
    private static final String inputFile = "input.txt";
    private static final String tokenOutputFile = "lexer.txt";

    private static final Path inputPath = Path.of(inputFile);
    private static final Path tokenOutputPath = Path.of(tokenOutputFile);

    private static BufferedWriter tokenWriter;
    private static BufferedReader reader;

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
    public static void printToken(ArrayList<Token> tokens){
        for(Token token : tokens){
            try{
                tokenWriter.write(token.getTokenType()+" "+token.getTokenContent());
                tokenWriter.newLine();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

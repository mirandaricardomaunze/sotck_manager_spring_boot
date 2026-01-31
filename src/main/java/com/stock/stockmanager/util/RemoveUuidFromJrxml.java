package com.stock.stockmanager.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class RemoveUuidFromJrxml {

    public static void main(String[] args) {
        try {
            ClassLoader classLoader = RemoveUuidFromJrxml.class.getClassLoader();
            File folder = new File(classLoader.getResource("reports/jrxml").getFile());

            Files.walk(folder.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".jrxml"))
                    .forEach(RemoveUuidFromJrxml::removeUuidFromFile);

            System.out.println("Todos os UUIDs foram removidos!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeUuidFromFile(Path path) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            content = content.replaceAll("\\s+uuid=\"[^\"]*\"", "");
            Files.writeString(path, content, StandardCharsets.UTF_8);
            System.out.println("UUID removido de: " + path);
        } catch (IOException e) {
            System.err.println("Erro ao processar: " + path);
            e.printStackTrace();
        }
    }
}

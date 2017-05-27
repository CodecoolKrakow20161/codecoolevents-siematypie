package utils;

import java.io.*;
import java.util.stream.Collectors;

public class FileReader {
    public String getStringFromFile(String pathToResource){
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(pathToResource);
        return new BufferedReader(new InputStreamReader(in))
                .lines().collect(Collectors.joining("\n"));
    }
}

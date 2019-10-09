package me.dasneueupdate.plotfeedback.utils;

import java.util.*;
import java.util.function.IntFunction;
import java.io.*;
import java.nio.file.*;
import java.awt.*;

public final class FileUtils
{
    private static final String NEW_LINE = "\r\n";
    
    private static byte[] readData(final InputStream in) {
        try {
            final int length = in.available();
            final byte[] data = new byte[length];
            in.read(data);
            return data;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static String[] readLines(final InputStream in) {
        final InputStreamReader inReader = new InputStreamReader(in);
        final BufferedReader reader = new BufferedReader(inReader);
        final ArrayList<String> lines = new ArrayList<String>();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        final String[] linesArray = lines.stream().toArray(new IntFunction<String[]>() {
            @Override
            public String[] apply(int size) {
                return new String[size];
            }
        });
        return linesArray;
    }
    
    public static byte[] readInternalData(final String path) {
        final InputStream in = FileUtils.class.getResourceAsStream(path);
        return readData(in);
    }
    
    public static byte[] readExternalData(final File file) {
        try {
            final FileInputStream in = new FileInputStream(file);
            return readData(in);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static byte[] readExternalData(final String path) {
        final File file = new File(path);
        return readExternalData(file);
    }
    
    public static String[] readInternalLines(final String path) {
        final InputStream in = FileUtils.class.getResourceAsStream(path);
        String[] array = readLines(in);
        try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return array;
    }
    
    public static String[] readExternalLines(final File file) {
        try {
            final FileInputStream in = new FileInputStream(file);
            return readLines(in);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String[] readExternalLines(final String path) {
        final File file = new File(path);
        return readExternalLines(file);
    }
    
    public static void writeData(final File file, final byte[] data) {
        try {
            final FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void writeData(final String path, final byte[] data) {
        final File file = new File(path);
        writeData(file, data);
    }
    
    public static void writeLines(final File file, final String... lines) {
        try {
            final FileOutputStream out = new FileOutputStream(file);
            final OutputStreamWriter outWriter = new OutputStreamWriter(out);
            final BufferedWriter writer = new BufferedWriter(outWriter);
            for (final String line : lines) {
                writer.write(String.valueOf(line) + "\r\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void writeLines(final String path, final String... lines) {
        final File file = new File(path);
        writeLines(file, lines);
    }
    
    public static void createFile(final String path) {
        final File file = new File(path);
        final File parent = file.getParentFile();
        if (parent != null) {
            createFolder(parent.getPath());
        }
        try {
            file.createNewFile();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void createFolder(final String path) {
        final File file = new File(path);
        file.mkdirs();
    }
    
    public static boolean copyFile(final File source, final File destination) {
        final Path sourcePath = source.toPath();
        final Path destinationPath = destination.toPath();
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean copyFile(final String source, final String destination) {
        final File sourceFile = new File(source);
        final File destinationFile = new File(destination);
        return copyFile(sourceFile, destinationFile);
    }
    
    public static boolean executeFile(final File file) {
        final boolean desktopSupported = Desktop.isDesktopSupported();
        if (desktopSupported && file.exists()) {
            final Desktop desktop = Desktop.getDesktop();
            final boolean canOpen = desktop.isSupported(Desktop.Action.OPEN);
            if (canOpen) {
                try {
                    desktop.open(file);
                    return true;
                }
                catch (IOException ex) {}
            }
        }
        return false;
    }
    
    public static boolean executeFile(final String path) {
        final File file = new File(path);
        final boolean desktopSupported = Desktop.isDesktopSupported();
        if (desktopSupported && file.exists()) {
            final Desktop desktop = Desktop.getDesktop();
            final boolean canOpen = desktop.isSupported(Desktop.Action.OPEN);
            if (canOpen) {
                try {
                    desktop.open(file);
                    return true;
                }
                catch (IOException ex) {}
            }
        }
        return false;
    }
}
package com.srcskyframework.helper;

import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Callback;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:36
 * Email: z82422@gmail.com
 * 文件管理 辅助对象
 */
public class FileHelper {
    private final static Logger logger = Logger.getLogger(FileHelper.class);

    public final static File TEMP_DIRECTORY = new File(Configuration.get().getString("web_fileuploader_tempdirectory"));
    public final static File DIRECTORY = new File(Configuration.get().getString("web_fileuploader_directory"));

    static {
        if (!TEMP_DIRECTORY.exists()) TEMP_DIRECTORY.mkdirs();
        if (!DIRECTORY.exists()) DIRECTORY.mkdirs();
    }

    public static final Pattern dosSeperator = Pattern.compile("\\\\");
    public static final Pattern lastSeperator = Pattern.compile("/$");

    public static String getFileNameChop(String fullpath) {
        if (fullpath == null) return null;
        fullpath = dosSeperator.matcher(fullpath).replaceAll("/");
        int pos = fullpath.lastIndexOf("/");
        if (pos > -1) return fullpath.substring(pos + 1);
        else return fullpath;
    }

    public static String getFilePathChop(String fullpath) {
        if (fullpath == null) return null;
        fullpath = dosSeperator.matcher(fullpath).replaceAll("/");
        int pos = fullpath.lastIndexOf("/");
        if (pos > -1) return fullpath.substring(0, pos + 1);
        else return "./";
    }


    public static String getCompleteLeadingSeperator(String fullpath) {
        if (fullpath == null) return null;
        fullpath = dosSeperator.matcher(fullpath).replaceAll("/");
        if (!fullpath.endsWith(File.separator)) fullpath = fullpath + "/";
        return fullpath;
    }

    public static String getRemoveLeadingSeperator(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            fileName = dosSeperator.matcher(fileName).replaceAll("/");
            fileName = lastSeperator.matcher(fileName).replaceAll("");
            return fileName;
        }
    }

    public static String getFilesizeString(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String readStream(InputStream stream, String... charset) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            List<String> lines = IOUtils.readLines(stream, ValidHelper.isEmpty(charset) ? "UTF-8" : charset[0]);
            for (int i = 0; i < lines.size(); i++) {
                strBuffer.append(lines.get(i));
                if (i < lines.size()) {
                    strBuffer.append("\n");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strBuffer.toString();
    }

    /**
     * <b>功能:</b>彻底删除一个文件（如果是目录，则目录下的内容也将被删除）
     *
     * @param file:一个文件（或目录）
     */
    public static final void delFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                delFile(files[i]);
            }
        }
        file.delete();
    }

    public static final void delFile(File[] files) {
        for (File file : files) {
            delFile(file);
        }
    }

    /**
     * <b>功能:</b>文件（目录）拷贝
     *
     * @param file1：源文件（或目录）
     * @param file2：目标文件（或目录）
     */
    /* 日期			版本		修订内容						修订人
    * 2006.02.19	1.0.0	加版本号						钱前
    */
    public static String copyFile(File file1, File file2) {
        String log = "";
        if (!file1.exists()) return log += "源文件不存在:" + file1.getPath();
        /*构建目标文件夹*/
        if (!file2.getParentFile().exists()) file2.getParentFile().mkdirs();
        if (file1.isDirectory()) {
            file2.mkdir();
            log += "建文件夹:" + file2.getPath() + "\n";
            File[] files = file1.listFiles();
            for (int i = 0; i < files.length; i++) {
                File ifile = files[i];
                String name = file2.getPath() + "/" + ifile.getName();
                File jfile = new File(name);
                log += copyFile(ifile, jfile);
            }
        } else {
            try {
                writeFile(file2, new FileInputStream(file1));
                log += "拷贝文件:" + file1.getName() + "\t>>  " + file2.getPath() + "\n";
            } catch (Exception e) {
                throw new RuntimeException("拷贝文件失败:" + log + "," + file1 + "到" + file2);
            }
        }
        return log;
    }

    public static boolean moveFile(File source, File target) {
        try {
            if (target.exists()) {
                target.delete();
            }
            FileUtils.moveFile(source, target);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * <b>功能： </b>获得文件的扩展名（不带.)
     *
     * @param fileName
     * @return ext（不带.)
     */
    private static String[] postfixs = new String[]{"JPG", "PNG", "JPEG", "GIF", "DOC", "DOCX", "XLS", "XLSX", "TXT", "ZIP", "RAR", "ISO"};

    public static String getPostfix(String fileName) {
        if (ValidHelper.isEmpty(fileName)) throw new RuntimeException("获得文件名称的后缀失败,参数为空:" + fileName + ".");
        try {
            if (fileName.indexOf("?") != -1) {
                fileName = fileName.substring(0, fileName.lastIndexOf("?"));
            }
            if (fileName.lastIndexOf(".") != -1) {
                return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
            } else if (fileName.lastIndexOf("?") != -1) {
                return fileName.substring(fileName.lastIndexOf("?") + 1).toUpperCase();
            } else {
                for (String postfix : postfixs) {
                    if (fileName.toUpperCase().endsWith(postfix)) {
                        return postfix;
                    }
                }
                return "TEMP";
            }
        } catch (Exception ex) {
            throw new RuntimeException("获得文件名称的后缀失败,名称不存在后缀:" + fileName + ".");
        }
    }

    // 创建文件夹
    public static File mkdirs(String file1, String file2) {
        File file = new File(file1, file2);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public static void writeFile(String filepath, InputStream stream) throws Exception {
        writeFile(new File(filepath), stream);
    }

    public static void writeFile(File file, InputStream stream) throws Exception {
        if (file.exists()) file.delete();
        try {
            FileOutputStream output = new FileOutputStream(file);
            try {
                IOUtils.copy(stream, output);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }


    //=======================FileUploader======================================================================Start
    // 生成临时文件 保存路径
    private static File getTempFile(String output) {
        File tempFile = new File(TEMP_DIRECTORY, System.currentTimeMillis() + "." + FileHelper.getPostfix(output));
        return tempFile;
    }

    // 生成 相对的 文件存放路径
    public static String generateDirectory(Long fileid) {
        StringBuffer directoryBuffer = new StringBuffer();
        directoryBuffer.append(fileid / (1024 * 1024 * 1024)).append("/");
        directoryBuffer.append(fileid / (1024 * 1024)).append("/");
        directoryBuffer.append(fileid / 1024).append("/");
        return directoryBuffer.toString();
    }

    // 生成 绝对的 文件存放路径
    public static String generateDirectoryAbsolute(Long fileid) {
        String directoryPath = generateDirectory(fileid);
        File directory = new File(DIRECTORY, directoryPath);
        if (!directory.exists()) directory.mkdirs();
        return directory.getPath();
    }

    public static File generateDirectoryAbsoluteFull(Long fileid, String postfix) {
        File file = new File(generateDirectoryAbsolute(fileid), null == postfix ? String.valueOf(fileid) : (fileid + "." + postfix.toUpperCase()));
        return file;
    }

    public static File generateDirectoryAbsoluteFull(Long fileid) {
        return generateDirectoryAbsoluteFull(fileid, null);
    }

    public static void deleteByResourceId(long id) {
        deleteByResourceId(id, true);
    }

    public static void deleteByResourceId(final long id, final boolean delSrcFile) {
        File directory = new File(generateDirectoryAbsolute(id));
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if ((delSrcFile && name.equals(id)) || name.startsWith(id + "_")) {
                    return true;
                }
                return false;
            }
        });
        for (File file : files) {
            file.delete();
            logger.debug("完成文件删除:" + file.getAbsolutePath());
        }
    }


    // 保存下载文件到临时目录
    private static HttpClient httpClient = new HttpClient();

    public static <T> T downloader(String url, Callback<T> callback) {
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(5000);
        GetMethod method = new GetMethod(URLHelper.encodeChineseParams(url, "UTF-8"));
        InputStream inputStream = null;
        try {
            int index = url.indexOf("/", 7);
            httpClient.getHostConfiguration().setHost(index == -1 ? url.substring(7) : url.substring(7, index));
            method.getParams().setSoTimeout(5000);
            method.setRequestHeader("Referer", url);
            method.setRequestHeader("Connection", "close");
            httpClient.executeMethod(method);
            inputStream = method.getResponseBodyAsStream();
            return callback.execute(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    IOUtils.closeQuietly(inputStream);
                }
                method.releaseConnection();
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static File writeFileDownloader(String input) {
        return writeFileDownloader(input, null);
    }

    public static File writeFileDownloader(String input, String savepath) {
        final File saveFile = null != savepath ? new File(savepath) : getTempFile(input);
        try {
            return downloader(input, new Callback<File>() {
                public File execute(Object stream) {
                    try {
                        FileHelper.writeFile(saveFile, (InputStream) stream);
                        return saveFile;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    // 保存上传文件 到临时目录
    public static File writeFileUploader(FileItem input) {
        try {
            File saveFile = getTempFile(input.getName());
            input.write(saveFile);
            return saveFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static File writeFileUploader(FileItemStream input) {
        try {
            File saveFile = getTempFile(input.getName());
            // 开始把文件写到你指定的上传文件夹
            Streams.copy(input.openStream(), new FileOutputStream(saveFile), true);
            return saveFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static File[] getUploaderFiles(String path) {
        String filename = getFileNameChop(path).replace("." + getPostfix(path), "");
        String postfix = getPostfix(path);
        final File source = new File(path);
        final String regex = "(?i)" + filename + "(\\_[0-9]+x[0-9]+)?\\." + postfix;
        return source.getParentFile().listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().matches(regex));
            }
        });
    }

    // 从input里面读取数据然后写入output，读完后自动关闭流
    public static void write(InputStream input, OutputStream output) throws IOException {
        write(input, output, true);
    }

    // 自动从input里面读数据，然后写到output里面去
    public static void write(InputStream input, OutputStream output, boolean close) throws IOException {
        byte[] b = new byte[1024];
        int len = input.read(b);
        while (len != -1) {
            output.write(b, 0, len);
            len = input.read(b);
        }
        output.flush();
        if (close) {
            input.close();
            output.close();
        }
    }


    public static String readHttp(String address, String charset) {
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            String result = readStream(conn.getInputStream(), charset);
            conn.disconnect();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static List<File> recursion(List<File> files, File parent, FileFilter filter) {
        if (null == files) {
            files = new ArrayList<File>();
        }
        if (parent.isDirectory()) {
            File[] cFile = null == filter ? parent.listFiles() : parent.listFiles(filter);
            for (File file : cFile) {
                recursion(files, file, filter);
            }
        } else {
            files.add(parent);
        }
        return files;
    }
}

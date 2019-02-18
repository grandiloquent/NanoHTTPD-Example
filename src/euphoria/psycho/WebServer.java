package euphoria.psycho;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static euphoria.psycho.FileUtils.sortFiles;

public class WebServer extends NanoHTTPD {

    public static final String UTF8 = "utf-8";
    private File mStaticDirectory;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public WebServer(int port) {
        super(port);
        initialize();
    }

    public WebServer(String hostname, int port) {
        super(hostname, port);
        initialize();
    }

    private Response generateDirectoryPage(File dir, int sortBy) {

        File[] children = dir.listFiles();
        sortFiles(children, sortBy, true);
        byte[][] indexBytes = DataProvider.getIndex();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(indexBytes[0]);
            os.write(StringUtils.escapeHTML(dir.getAbsolutePath()).getBytes(UTF8));
            os.write(indexBytes[1]);
            os.write(generateFileInfos(children));
            os.write(indexBytes[2]);
            os.write(Integer.toString(children.length).getBytes(UTF8));
            os.write(indexBytes[3]);

        } catch (IOException e) {
            return getInternalErrorResponse(e.getMessage());
        }
        return Response.newFixedLengthResponse(Status.OK,
                MimeUtils.guessMimeTypeFromExtension("html"),
                os.toByteArray());
    }

    private byte[] generateFileInfos(File[] files) {

        byte[][] bytes = DataProvider.getFileList();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (File file : files) {
            try {
                os.write(bytes[0]);
                if (file.isDirectory()) {
                    os.write("icon-file-m".getBytes(UTF8));
                } else {
                    os.write("icon-tdoc-m".getBytes(UTF8));
                }
                os.write(bytes[1]);
                os.write("/files?p=".getBytes(UTF8));
                os.write(encodeURI(file.getAbsolutePath()));
                os.write(bytes[2]);
                os.write(file.getName().getBytes(UTF8));
                os.write(bytes[3]);
                os.write(mSimpleDateFormat.format(file.lastModified()).getBytes(UTF8));
                os.write(bytes[4]);
                if (file.isFile())
                    os.write(FileUtils.formatFileSize(file.length()).getBytes(UTF8));
                os.write(bytes[5]);

            } catch (IOException e) {
            }
        }


        return os.toByteArray();

    }

    public String getURL() {
        return getHostname() + ":" + myPort;
    }

    private Response handleIndex() {

        File dir = new File("c:\\");

        return generateDirectoryPage(dir, 1);

    }

    private Response handleQueryFile(Map<String, List<String>> parameters) {

        try {
            String path = URLDecoder.decode(ServerUtils.getStringFromMap(parameters, "p", null), UTF8);
            File file = new File(path);
            if (file.isDirectory()) {
                int sortBy = ServerUtils.getIntFromMap(parameters, "sort", 0);
                return generateDirectoryPage(file, sortBy);
            } else if (file.isFile()) {
                String ext = FileUtils.getExtension(file.getName());
                switch (ext) {
                    case "mp4":
                        return handleVideo(file);
                }
            }
            return getNotFoundResponse();
        } catch (UnsupportedEncodingException e) {
            return getInternalErrorResponse(e.getMessage());
        }

    }

    private Response handleStaticFile(String uri) {
        int dotIndex = uri.lastIndexOf('.');
        if (dotIndex == -1) return null;
        String extension = uri.substring(dotIndex + 1);
        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);
        File file = new File(mStaticDirectory, uri);
        if (!file.exists()) return getNotFoundResponse();
        try {

            FileInputStream is = new FileInputStream(file);
            Response response = Response.newFixedLengthResponse(Status.OK, mimeType, is, file.length());
            addHeaderForFile(response, file);
            return response;
        } catch (FileNotFoundException e) {
            return getInternalErrorResponse(e.getMessage());
        }
    }

    private Response handleVideo(File file) {

        try {
            FileInputStream is = new FileInputStream(file);

            Response response = Response.newFixedLengthResponse(Status.OK,
                    ServerUtils.getMimeType(file.getName(), "*/*"), is, file.length());
            response.addHeader(ServerUtils.HTTP_ACCEPT_RANGES, "bytes");
            response.addHeader(ServerUtils.HTTP_CONTENT_RANGE, "bytes 0-" + (file.length() - 1) + "/" + file.length());
            response.addHeader(ServerUtils.HTTP_ETAG, ServerUtils.getEtag(file));
            return response;
        } catch (Exception e) {
            return getInternalErrorResponse(e.getMessage());
        }

    }

    private void initialize() {
        setHTTPHandler(this::respond);
    }

    // 主要方法
    private Response respond(IHTTPSession input) {
        String uri = input.getUri();
        Map<String, List<String>> parameters = null;

        System.out.println("URI = " + uri);
        if (uri.equals("/")) {
            //return serveFile("/index.html");
            return handleIndex();
        } else if (uri.indexOf('.') != -1) {

            Response response = handleStaticFile(uri);
            if (response != null) {
                return response;
            }
        } else {

            if (parameters == null) ;
            parameters = input.getParameters();
            if (parameters.containsKey("p")) {
                Response response = handleQueryFile(parameters);
                if (response != null) return response;
            }

        }
        return getNotFoundResponse();

    }

    public void setStaticDirectory(File staticDirectory) {
        mStaticDirectory = staticDirectory;
    }


    private static void addHeaderForFile(Response response, File file) {
        response.addHeader(ServerUtils.HTTP_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        String ext = FileUtils.getExtension(file.getName());

        if (ext == null) return;
        switch (ext.toLowerCase()) {
            case "svg":
            case "png":
                response.addHeader(ServerUtils.HTTP_CACHE_CONTROL, "max-age=" + 259200 /*3 Days*/);
                response.addHeader(ServerUtils.HTTP_EXPIRES, ServerUtils.getGMTDateTime(259200));
                break;
        }

        response.addHeader(ServerUtils.HTTP_CONNECTION, "close");


    }

    private static byte[] encodeURI(String str) {
        try {
            return URLEncoder.encode(str, UTF8).getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Response getInternalErrorResponse(String s) {
        return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERROR: " + s);
    }

    private static Response getNotFoundResponse() {
        return Response.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");
    }


}

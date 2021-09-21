package encryption;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Controller {

    private final String crlf = "\r\n";
    private URL url;
    private URLConnection urlConnection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String[] fileNames;
    private String output;
    private String boundary;
    private final int bufferSize = 4096;

    void fuu(){

        try {
//            url = new URL("http://banglachi.org/unmochon/upload-manager.php");
            url = new URL("http://formulabd.com/image");
            boundary = "---------------------------4664151417711";
            System.out.println("url:" + url);
            URLConnection urlCon = url.openConnection();
            HttpURLConnection urlConnection = (HttpURLConnection) urlCon;
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            String postData = "";
//            String fileName = fileNames[0];
//            String fileName = "I:\\Codes\\Java programming\\Unmochon_2020\\imageUploadTest\\tree.jpg";
//            String fileName = "src/sample/kurama.jpg";
            String fileName = "kurama.encrypted";
            InputStream fileInputStream = new FileInputStream(fileName);

            byte[] fileData = new byte[fileInputStream.available()];
            fileInputStream.read(fileData);

            // ::::: PART 1 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            String part1 = "";
            part1 += "--" + boundary + crlf;
//            File f = new File(fileNames[0]);
            File f = new File(fileName);
            fileName = f.getName(); // we do not want the whole path, just the name
            part1 += "Content-Disposition: form-data; name=\"image_data\"; filename=\"" + fileName + "\""
                    + crlf;

            // CONTENT-TYPE
            // TODO: add proper MIME support here
            if (fileName.endsWith("png")) {
                part1 += "Content-Type: image/png" + crlf;
            } else {
                part1 += "Content-Type: image/jpeg" + crlf;
            }

            part1 += crlf;
            System.out.println(part1);
            // File's binary data will be sent after this part

            // ::::: PART 2 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            String message3 ="";
            message3 += "Content-Disposition: form-data; name=\"userid\""+crlf+crlf;
            message3 +=URLEncoder.encode("userid","UTF-8");
            message3 += crlf;


            String part2 = crlf + "--" + boundary + "--" + crlf;



            System.out.println("Content-Length"
                    +  String.valueOf(part1.length() +  part2.length()+ message3.length() + part2.length() + fileData.length));
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(part1.length() +  part2.length() + message3.length()+ part2.length() + fileData.length));


            // ::::: File send ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            outputStream = urlConnection.getOutputStream();
            outputStream.write(part1.getBytes());

            int index = 0;
            int size = bufferSize;
            do {
                System.out.println("wrote " + index + "b");
                if ((index + size) > fileData.length) {
                    size = fileData.length - index;
                }
                outputStream.write(fileData, index, size);
                index += size;
            } while (index < fileData.length);
            System.out.println("wrote " + index + "b");

//            System.out.println(message3);
//            outputStream.write(message3.getBytes(StandardCharsets.UTF_8));
//            sendField(outputStream, "userid", "userid");
            System.out.println(part2);
            outputStream.write(part2.getBytes());
            System.out.println(message3);
//            outputStream.write(message3.getBytes(StandardCharsets.UTF_8));
            sendField(outputStream, "userid", "userid");
            System.out.println(part2);
            outputStream.write(part2.getBytes());

            outputStream.flush();

            // ::::: Download result into the 'output' String :::::::::::::::::::::::::::::::::::::::::::::::
            inputStream = urlConnection.getInputStream();
            StringBuilder sb = new StringBuilder();
            char buff = 512;
            int len;
            byte[] data = new byte[buff];
            do {

                len = inputStream.read(data);

                if (len > 0) {
                    sb.append(new String(data, 0, len));
                }
            } while (len > 0);
            output = sb.toString();

            System.out.println(output);
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Close connection");
            try {
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void sendFile(OutputStream out, String name, InputStream in, String fileName) throws IOException {
        String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name,"UTF-8")
                + "\"; filename=\"" + URLEncoder.encode(fileName,"UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[2048];
        for (int n = 0; n >= 0; n = in.read(buffer))
            out.write(buffer, 0, n);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
    private void sendField(OutputStream out, String name, String field) throws IOException {
        String o = "Content-Disposition: form-data; name=\""
                + URLEncoder.encode(name,"UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        out.write(URLEncoder.encode(field,"UTF-8").getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

}
package com.example.stayathome.server;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class ServerBackground extends AsyncTask<String, Void, String> {

    private Context context;

    public ServerBackground(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String assignmentAddress = "http://stay4tree.website/assignment.php";
        String registrationAddress = "http://stay4tree.website/registration.php";
        String type = params[0];

        if (type.equals("assignment")) {
            try {
                //extract content to send
                String userName = params[1];
                String project = params[2];
                //establish connection to server and pass data to php file
                URL url = new URL(assignmentAddress);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream getAssignment = httpURLConnection.getOutputStream();
                BufferedWriter writeAssignment = new BufferedWriter(new OutputStreamWriter(getAssignment, StandardCharsets.UTF_8));
                String postAssignment = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
                        + "&" + URLEncoder.encode("project", "UTF-8") + "=" + URLEncoder.encode(project, "UTF-8");
                writeAssignment.write(postAssignment);
                writeAssignment.flush();
                writeAssignment.close();
                getAssignment.close();
                //get response
                InputStream getResponse = httpURLConnection.getInputStream();
                BufferedReader readResponse = new BufferedReader(new InputStreamReader(getResponse, StandardCharsets.ISO_8859_1));
                String result = "", line = "";

                while ((line = readResponse.readLine()) != null) {
                    result += line;
                }
                readResponse.close();
                getResponse.close();
                httpURLConnection.disconnect();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("registration")) {
            try {
                //extract content to send
                String  userName = params[1];
                //establish connection to server pass data to php file
                URL url = new URL(registrationAddress);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String postData = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

package com.tv.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class FFMpegUtils {
    public String path;
    public FFMpegUtils(String path){
        this.path = path;
    }
    public void mergeVideoMp3(String videoInputPath,String mp3InputPath,String videoOutputPath,double videoSeconds) throws IOException {
        ProcessBuilder process = new ProcessBuilder();
        List<String> commands = new ArrayList<String>();
        commands.add(path);
        commands.add("-i");
        commands.add(videoInputPath);
        commands.add("-i");
        commands.add(mp3InputPath);
        commands.add("-t");
        commands.add(String.valueOf(videoSeconds));
        commands.add("-y");
        commands.add(videoOutputPath);
            Process process1 = process.command(commands).start();
            InputStream errorStream = process1.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = "";
            while ( (line = br.readLine()) != null ) {
            }
            if (br != null) {
                br.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (errorStream != null) {
                errorStream.close();
            }
    }

    public void getVideoImg(String videoInputPath,String imgOutputPath) throws IOException {
        ProcessBuilder process = new ProcessBuilder();
        List<String> commands = new ArrayList<String>();
        commands.add(path);
        commands.add("-ss");
        commands.add("00:00:01");
        commands.add("-y");
        commands.add("-i");
        commands.add(videoInputPath);
        commands.add("-vframes");
        commands.add("1");
        commands.add(imgOutputPath);
        for (String c : commands) {
            System.out.print(c + " ");
        }
            Process process1 = process.command(commands).start();
            InputStream errorStream = process1.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = "";
            while ( (line = br.readLine()) != null ) {
            }
            if (br != null) {
                br.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (errorStream != null) {
                errorStream.close();
            }
    }

    public static void main(String[] args){

    }
}

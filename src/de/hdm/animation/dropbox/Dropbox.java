/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import de.hdm.animation.DirectoryAnimationPanel;

public class Dropbox {
    private String ACCESS_TOKEN = "";
    private DirectoryAnimationPanel animationPanel;

    public Dropbox(String accessToken, DirectoryAnimationPanel dap) {
        this.ACCESS_TOKEN = accessToken;
        this.animationPanel = dap;
    }

    public static void main(String[] args) {
        try {
            new Dropbox("NxHgZgl9b78AAAAAAAANaCPIjq6PfQvhmAx7RXHFzyfWoc29pH3lIGhd5P3YzFwF", null).showFiles();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public void showAccount() throws DbxException {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("HdM/Sharing").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());
    }

    private DbxUserFilesRequests getFilesRequests() {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("HdM/Sharing").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        return client.files();
    }

    public void showFiles() throws DbxException {
        DbxUserFilesRequests filesRequests = getFilesRequests();
        // Get files and folder metadata from Dropbox root directory
        showFiles(filesRequests, filesRequests.listFolder(""));
    }

    private void showFiles(DbxUserFilesRequests filesRequests, ListFolderResult result) throws DbxException {
        for (Metadata metadata : result.getEntries()) {
            System.out.println(metadata.getPathLower());
            if (metadata instanceof FolderMetadata) {
                showFiles(filesRequests, filesRequests.listFolder(metadata.getPathLower()));
            }
        }

        if (result.getHasMore()) {
            showFiles(filesRequests, filesRequests.listFolderContinue(result.getCursor()));
        }
    }

    public void downloadFiles(File targetDirectory) throws DbxException {
        DbxUserFilesRequests filesRequests = getFilesRequests();
        new Thread() {
            public void run() {
                try {
                    downloadFiles(targetDirectory, filesRequests, filesRequests.listFolder(""));
                } catch (ListFolderErrorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (DbxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
        
    }

    private void downloadFiles(File targetDirectory, DbxUserFilesRequests filesRequests, ListFolderResult result)
            throws DbxException {

        File targetFile;
        FileOutputStream out;
        for (Metadata md : result.getEntries()) {
            if (md instanceof FolderMetadata) {
                targetFile = new File(targetDirectory, md.getName());
                targetFile.mkdir();
                downloadFiles(targetFile, filesRequests, filesRequests.listFolder(md.getPathLower()));
            } else {
                targetFile = new File(targetDirectory, md.getName());
                try {
                    out = new FileOutputStream(targetFile);
                    animationPanel.addFile(targetFile);
                    System.out.println("downloading " + targetFile.getName());
                    filesRequests.download(md.getPathLower()).download(out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (result.getHasMore()) {
            downloadFiles(targetDirectory, filesRequests, filesRequests.listFolderContinue(result.getCursor()));
        }
    }
    
    public void uploadFiles(File sourceDirectory) throws DbxException {
        DbxUserFilesRequests filesRequests = getFilesRequests();
        new Thread() {
            public void run() {
                try {
                    uploadFiles(sourceDirectory, "", filesRequests);
                    animationPanel.reset();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void uploadFiles(File sourceDirectory, String targetPath, DbxUserFilesRequests filesRequests) throws DbxException {

        // Get files and folder to Dropbox root directory
        for (File file : sourceDirectory.listFiles()) {
            if (!file.isDirectory()) {
                try (InputStream in = new FileInputStream(file)) {
                    animationPanel.hideFile(file);
                    System.out.println("uploading " + file.getName() + " to " + targetPath);
                    filesRequests.uploadBuilder(targetPath + "/" + file.getName()).uploadAndFinish(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String newTargetPath = targetPath + "/" + file.getName();
                filesRequests.createFolder(newTargetPath);
                uploadFiles(file, newTargetPath, filesRequests);
            }
        }
    }
}

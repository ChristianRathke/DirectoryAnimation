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
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.users.FullAccount;

import de.hdm.animation.DirectoryAnimationPanel;

public class Dropbox {
    private String accessToken = "";
    private DirectoryAnimationPanel animationPanel = DirectoryAnimationPanel.placeHolder;

    public Dropbox(String accessToken, DirectoryAnimationPanel dap) {
        this(accessToken);
        this.animationPanel = dap;
    }
    
    public Dropbox(String accessToken) {
        this.accessToken = accessToken;
    }

    public static void main(String[] args) {
        try {
            Dropbox dbx = new Dropbox("NxHgZgl9b78AAAAAAAANaCPIjq6PfQvhmAx7RXHFzyfWoc29pH3lIGhd5P3YzFwF", null);
            dbx.showAccount();
            dbx.showFiles();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public void showAccount() throws DbxException {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("HdM/Sharing").build();
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());
    }
    
    public DbxClientV2 getClient() {
     // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("HdM/Sharing").build();
        DbxClientV2 client = new DbxClientV2(config, accessToken);
        return client;
    }
    
    public FullAccount getAccount() throws DbxException {
        return getClient().users().getCurrentAccount();
    }

    private DbxUserFilesRequests getFilesRequests() {
        return getClient().files();
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
    
    public Dropbox withAnimationPanel(DirectoryAnimationPanel panel) {
        animationPanel = panel;
        return this;
    }

    public void downloadFiles(File targetDirectory) throws DbxException {
        DbxUserFilesRequests filesRequests = getFilesRequests();
        try {
            downloadFiles(targetDirectory, filesRequests, filesRequests.listFolder(""));
        } catch (ListFolderErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
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
                if (targetFile.listFiles().length == 0) {
                    // empty folders are deleted right away
                    targetFile.delete();
                }
            } else {
                targetFile = new File(targetDirectory, md.getName());
                try {
                    out = new FileOutputStream(targetFile);
                    System.out.println("downloading " + targetFile.getName());
                    filesRequests.download(md.getPathLower()).download(out);
                    animationPanel.addFile(targetFile);
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
        try {
            uploadFiles(sourceDirectory, "", filesRequests);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    private void uploadFiles(File sourceDirectory, String targetPath, DbxUserFilesRequests filesRequests)
            throws DbxException {

        // Get files and folder to Dropbox root directory
        for (File file : sourceDirectory.listFiles()) {
            if (!file.isDirectory()) {
                try (InputStream in = new FileInputStream(file)) {
                    // this will remove the file label for the file only
                    animationPanel.shrinkFile(file);
                    System.out.println("uploading " + file.getName() + " to " + targetPath);
                    try {
                        filesRequests.uploadBuilder(targetPath + "/" + file.getName()).uploadAndFinish(in);
                    } catch (UploadErrorException e) {
                        System.out.println(e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String newTargetPath = targetPath + "/" + file.getName();
                try {
                    filesRequests.getMetadata(newTargetPath);
                } catch (GetMetadataErrorException e) {
                    System.out.println("Error to confirm absence of " + newTargetPath);
                    filesRequests.createFolder(newTargetPath);
                }
                uploadFiles(file, newTargetPath, filesRequests);
            }
        }
    }
}

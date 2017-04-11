package com.example;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GridFsFakeTest {

    public static void main(String[] args) throws FileNotFoundException {

        List<File> files = createFile();

        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase db = mongoClient.getDatabase("test");
        GridFSBucket gridFSBucket = GridFSBuckets.create(db);

//        gridFSBucket.drop();

        for (File file : files) {
            String fileName = file.getName();
            FileInputStream inputStream = new FileInputStream(file);

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(50000)
                    .metadata(new Document("type", "file").append("upload_date", new Date()));

            gridFSBucket.uploadFromStream(fileName, inputStream, options);

            break;
        }



//
//        MongoCollection<Document> collection = db.getCollection("fs_pmml.files");
//        MongoCollection<Document> collection1 = db.getCollection("fs_pmml.chunks");
//
//        if(collection != null){
//            MongoNamespace mongoNamespace = new MongoNamespace("fs_pmml" + System.currentTimeMillis() + ".files");
//            collection.renameCollection(mongoNamespace);
//        }
//
//        if(collection1 != null){
//            MongoNamespace mongoNamespace = new MongoNamespace("fs_pmml" + System.currentTimeMillis() + ".chunks");
//            collection1.renameCollection(mongoNamespace);
//        }


        GridFSFindIterable gridFSFiles = gridFSBucket.find();
        GridFSFile first = gridFSFiles.first();


    }

    private static List<File> createFile() {
        File folder = new File("C:\\Users\\Administrator\\Desktop\\tasks\\pmml_task\\testing\\pmml\\pmml_big_files\\bck");
        return Arrays.asList(folder.listFiles());
    }



}

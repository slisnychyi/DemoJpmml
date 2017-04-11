package gridfs;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Sorts.descending;

public class GridService {

    private static final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
    private static final MongoDatabase db = mongoClient.getDatabase("test");
    private static final GridFSBucket gridFSBucket = GridFSBuckets.create(db, "campaign_pmml");


    public static void main(String[] args) throws IOException, ClassNotFoundException {

        for (int i = 0; i < 10; i++) {
            insert(createFile());
            int i1 = lastVersion();
            System.out.println("version = " + i1);

        }

        List<String> files = read();

        dropLast(5);

    }

    private static void dropLast(int i) {
        int i1 = lastVersion();
        int lessThen = i1 - i;
        GridFSFindIterable gridFSFiles = gridFSBucket.find(lt("metadata.version", lessThen));
        for (GridFSFile gridFSFile : gridFSFiles) {
            ObjectId objectId = gridFSFile.getObjectId();
            gridFSBucket.delete(objectId);
        }
    }

    private static void insert(List<File> files) throws FileNotFoundException {

        int version = lastVersion();

        for (File file : files) {
            // Get the input stream
            InputStream streamToUploadFrom = new FileInputStream(file);
            // Create some custom options
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("version", ++version));

            ObjectId fileId = gridFSBucket.uploadFromStream(file.getName(), streamToUploadFrom, options);

            System.out.println("inserted = " + fileId);
        }

    }

    private static List<String> read() throws IOException, ClassNotFoundException {

        ArrayList<String> results = new ArrayList<>();

        int lastVersion = lastVersion();

        GridFSFindIterable gridFSFiles = gridFSBucket.find(eq("metadata.version", lastVersion));

        for (GridFSFile gridFSFile : gridFSFiles) {
            ObjectId objectId = gridFSFile.getObjectId();
            int fileLength = (int) gridFSFile.getLength();
            byte[] bytes = new byte[fileLength];
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(objectId);
            gridFSDownloadStream.read(bytes);

            String result = new String(bytes, StandardCharsets.UTF_8);
            results.add(result);
        }

        return results;
    }

    private static int lastVersion(){
        GridFSFile first = gridFSBucket.find().sort(descending("metadata.version")).first();
        if(Objects.nonNull(first)) {
            Object version = first.getMetadata().get("version");
            if (version != null) {
                return (int) version;
            }
        }
        return -1;
    }

    private static List<File> createFile() {
        File folder = new File("C:\\Users\\Administrator\\Desktop\\tasks\\pmml_task\\testing\\pmml\\pmml_big_files\\bck");
        return Arrays.asList(folder.listFiles());
    }

}

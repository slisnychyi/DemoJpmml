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
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.PMMLUtil;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Sorts.descending;
import static org.hamcrest.core.Is.is;

public class GridService {

    private static final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

//    private static final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://192.168.249.15:27017, 192.168.251.16:27017"));
    private static final MongoDatabase db = mongoClient.getDatabase("test");
    private static final GridFSBucket gridFSBucket = GridFSBuckets.create(db, "campaign_pmml");


    public static void main(String[] args) throws IOException, ClassNotFoundException, JAXBException, SAXException {

//        for (int i = 0; i < 1; i++) {
//            insert(createFile());
//            int i1 = lastVersion();
//            System.out.println("version = " + i1);
//
//        }

        List<String> files = read();

//        dropLast(5);

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
        version++;

        for (File file : files) {
            InputStream streamToUploadFrom = new FileInputStream(file);
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("version", version));

            ObjectId fileId = gridFSBucket.uploadFromStream(file.getName(), streamToUploadFrom, options);

            System.out.println("inserted = " + fileId);
        }

    }

    private static List<String> read() throws IOException, ClassNotFoundException, SAXException, JAXBException {

        ArrayList<String> results = new ArrayList<>();

        int lastVersion = lastVersion();

        GridFSFindIterable gridFSFiles = gridFSBucket.find(eq("metadata.version", lastVersion));

        for (GridFSFile gridFSFile : gridFSFiles) {
            ObjectId objectId = gridFSFile.getObjectId();
            int fileLength = (int) gridFSFile.getLength();
            byte[] bytes = new byte[fileLength];
            try(GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(objectId)){
                gridFSDownloadStream.read(bytes);
            }

            String result = new String(bytes, StandardCharsets.UTF_8);

            String evaluatorsFromFiles = createEvaluatorsFromFiles(gridFSFile.getFilename());

//            boolean equ = evaluatorsFromFiles.equalsIgnoreCase(result);
//            System.out.println(equ);

//            InputSource source = new InputSource(new StringReader(evaluatorsFromFiles));
            InputSource source = new InputSource(new ByteArrayInputStream(bytes));



            SAXSource filteredSource = ImportFilter.apply(source);

            try {
                PMML pmml = JAXBUtil.unmarshalPMML(filteredSource);

                Evaluator evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);

                System.out.println(evaluator);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("FILE CURRUPTED : " + gridFSFile);
            }

        }

        return results;
    }

    private static int lastVersion(){
        GridFSFile first = gridFSBucket.find().sort(descending("metadata.version")).first();
        if(Objects.nonNull(first)) {
            Object version = first.getMetadata().get("version");
            if (Objects.nonNull(version)) {
                return (int) version;
            }
        }
        return -1;
    }

    private static List<File> createFile() {
        File folder = new File("C:\\Users\\Administrator\\Desktop\\tasks\\pmml_task\\testing\\pmml\\pmmlfiles");
        return Arrays.asList(folder.listFiles());
    }

    private static String createEvaluatorsFromFiles(String fileName) throws FileNotFoundException {
        List<File> file = createFile();

        Optional<File> first = file.stream().filter(e -> e.getName().equalsIgnoreCase(fileName)).findFirst();

        if(first.isPresent()){

            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(first.get());
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }


            return stringBuilder.toString();

        }

        return null;

    }

    @Test
    public void test() throws FileNotFoundException, SAXException {
        int lastVersion = lastVersion();

        GridFSFindIterable gridFSFiles = gridFSBucket.find(eq("metadata.version", lastVersion));


        for (GridFSFile gridFSFile : gridFSFiles) {

            System.out.println(gridFSFile.getFilename());

            ObjectId objectId = gridFSFile.getObjectId();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(objectId, byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();

            String result = new String(bytes, StandardCharsets.UTF_8);

//            String result = byteArrayOutputStream.toString();
            InputSource source = new InputSource(new StringReader(result));
            SAXSource filteredSource = ImportFilter.apply(source);

            try {
                PMML pmml = JAXBUtil.unmarshalPMML(filteredSource);

                Evaluator evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);

                System.out.println(evaluator);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("FILE CURRUPTED : " + gridFSFile);
            }

        }


    }

}

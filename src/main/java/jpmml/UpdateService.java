package jpmml;

import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.jpmml.model.PMMLUtil;
import org.jpmml.model.visitors.LocatorNullifier;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class UpdateService {

    public static void main(String[] args) throws IOException, SAXException, JAXBException {

        Map<String, Evaluator> evaluators = upload();
        Evaluator evaluator = evaluators.get("1_new");

        List<InputField> inputFields = evaluator.getInputFields();

        Map<String, Object> values = new HashMap<>();
        //Map<String, String> values = prepareArguments();

        Map<FieldName, FieldValue> argums = new HashMap<>();

        for (InputField inputField : inputFields) {
            FieldName name = inputField.getName();
            String value = inputField.getName().getValue();
            Object rawData = values.get(value);

            FieldValue fieldValue = inputField.prepare(rawData);

            argums.put(name, fieldValue);

        }

        FieldName factor = new FieldName("factor");
        Object v = 0.1;
        FieldValue fieldValue = FieldValueUtil.create(DataType.DOUBLE, OpType.CONTINUOUS, v);
        //argums.put(factor, fieldValue);

        try{
            Map<FieldName, ?> result = evaluator.evaluate(argums);
            System.out.println("result = " + result);

        } catch (MissingValueException ex){
            ex.printStackTrace();
            System.out.println("Exception = " + ex);
            System.out.println(ex.getClass());

        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("exsss");
        }

    }

    private static Map<String, Evaluator> upload() throws FileNotFoundException, SAXException, JAXBException {
        Map<String, Evaluator> evaluators = Maps.newHashMap();

        File pmmlFolder = new File("C:\\Users\\Administrator\\Desktop\\pmml_template\\new");

        for (File fileEntry : pmmlFolder.listFiles()) {
            InputStream in = new FileInputStream(fileEntry);
            PMML pmml = PMMLUtil.unmarshal(in);
//            LocatorNullifier locatorNullifier = new LocatorNullifier();
//            locatorNullifier.applyTo(pmml);
            Evaluator evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
            String groupId = fileEntry.getName();
            groupId = groupId.substring(0 , groupId.indexOf("."));
            evaluators.put(groupId, evaluator);
        }

        return evaluators;
    }


    static Map<String, String> prepareArguments() throws IOException {
        Map<String, String> result = new HashMap<>();
        File file = new File("C:\\Users\\Administrator\\Desktop\\pmml_template\\input125xmlReduced.csv");
        CSVReader reader = new CSVReader(new FileReader(file));
        String [] nextLine;
        String[] headers = reader.readNext();
        Map<Integer, String> headerIndex = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            headerIndex.put(i, headers[i]);
        }

        while ((nextLine = reader.readNext()) != null) {
            Set<Integer> indexes = headerIndex.keySet();
            for (Integer index : indexes) {
                String value = nextLine[index];
                result.put(headerIndex.get(index), value);
            }
        }

        return result;
    }


}

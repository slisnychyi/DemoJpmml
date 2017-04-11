package jpmml;

import com.google.common.collect.Maps;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.PMMLUtil;
import org.jpmml.model.visitors.LocatorNullifier;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateService {

    public static void main(String[] args) throws FileNotFoundException, SAXException, JAXBException {

        Map<String, Evaluator> evaluators = upload();

        Evaluator evaluator = evaluators.get("1");

        List<InputField> inputFields = evaluator.getInputFields();

        Map<String, Object> values = new HashMap<>();
        values.put("a", "");
        values.put("cpm", 0.245);

//        Map<FieldName, FieldValue> argums = new HashMap<>();

//        for (InputField inputField : inputFields) {
//            FieldName name = inputField.getName();
//            String value = inputField.getName().getValue();
//            Object rawData = values.get(value);
//
//            FieldValue fieldValue = inputField.prepare(rawData);
//
//            argums.put(name, fieldValue);
//
//        }

        HashMap<FieldName, String> argums = new HashMap<>();

        argums.put(new FieldName("a"), "");
        argums.put(new FieldName("cpm"), "0.25");
//        argums.put(new FieldName("factor"), "2");

        Map<FieldName, ?> result = evaluator.evaluate(argums);

        System.out.println(result);
    }

    private static Map<String, Evaluator> upload() throws FileNotFoundException, SAXException, JAXBException {
        Map<String, Evaluator> evaluators = Maps.newHashMap();

        File pmmlFolder = new File("C:\\Users\\Administrator\\Desktop\\pmml_template\\new");

        for (File fileEntry : pmmlFolder.listFiles()) {
            InputStream in = new FileInputStream(fileEntry);
            PMML pmml = PMMLUtil.unmarshal(in);
            LocatorNullifier locatorNullifier = new LocatorNullifier();
            locatorNullifier.applyTo(pmml);

            Evaluator evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);
            String groupId = fileEntry.getName();
            groupId = groupId.substring(0 , groupId.indexOf("."));
            evaluators.put(groupId, evaluator);
        }

        return evaluators;
    }


}

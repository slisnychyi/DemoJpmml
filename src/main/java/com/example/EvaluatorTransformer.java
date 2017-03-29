package com.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.hppc.HppcModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.pcollections.PCollectionsModule;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.mining.MiningModelEvaluator;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.jpmml.model.visitors.LocatorNullifier;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.util.Map;

public class EvaluatorTransformer {

    Map<String, Evaluator> getEvaluators() throws FileNotFoundException, SAXException, JAXBException {
        Map<String, Evaluator> evaluators = Maps.newHashMap();

        File pmmlFolder = new File("C:\\Users\\Administrator\\Desktop\\tasks\\pmml_task\\testing\\pmml\\2");

        for (File fileEntry : pmmlFolder.listFiles()) {
                InputStream in = new FileInputStream(fileEntry);
                InputSource source = new InputSource(in);

                SAXSource filteredSource = ImportFilter.apply(source);

                PMML pmml = JAXBUtil.unmarshalPMML(filteredSource);

                LocatorNullifier locatorNullifier = new LocatorNullifier();
                locatorNullifier.applyTo(pmml);

                Evaluator evaluator = ModelEvaluatorFactory.newInstance().newModelEvaluator(pmml);

                String groupId = fileEntry.getName();
                groupId = groupId.substring(0 , groupId.indexOf("."));
                evaluators.put(groupId, evaluator);
        }

        return evaluators;
    }

    Evaluator executeEvaluation(String url) throws IOException, ClassNotFoundException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://127.0.0.1:8080/" + url);
        HttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        InputStream content = entity.getContent();

        String s = IOUtils.toString(content);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new GuavaModule())
                .registerModule(new HppcModule())
                .registerModule(new PCollectionsModule())
                .registerModule(new Jdk8Module())
                ;

        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);


        Evaluator evaluator = objectMapper.readValue(s, ModelEvaluator.class);

        return evaluator;
    }

    Evaluator executeEvaluationByte(String url) throws IOException, ClassNotFoundException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://127.0.0.1:8080/" + url);

        HttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        InputStream content = entity.getContent();

        byte[] bytes = IOUtils.toByteArray(content);

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

        Evaluator evaluator = (Evaluator) objectInputStream.readObject();

        System.out.println(evaluator);

        return evaluator;
    }


}

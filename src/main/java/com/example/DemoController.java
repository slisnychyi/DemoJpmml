package com.example;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.mining.MiningModelEvaluator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

@Controller
public class DemoController {

    private EvaluatorTransformer transformer;
    private TestEntityTransformer testEntityTransformer;
    private Map<String, MiningModelEvaluator> evaluators;
    private Map<String, PMML> pmmls;

    public DemoController() {
        this.testEntityTransformer = new TestEntityTransformer();
        this.transformer = new EvaluatorTransformer();
        try {
            this.evaluators = transformer.getEvaluators();
            this.pmmls = transformer.getPMMLS();
        } catch (FileNotFoundException | SAXException | JAXBException e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    @ResponseBody
    public String index(){
        return "Hello word";
    }

    @RequestMapping(value = { "/getEvaluator/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public Evaluator getEvaluator(@PathVariable("id") String id) throws IOException, URISyntaxException {
        return evaluators.get(id);
    }

    @RequestMapping(value = { "/getEvaluator2/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getEvaluator2(@PathVariable("id") String id) throws IOException, URISyntaxException {
        Evaluator eval = evaluators.get(id);
        return new ResponseEntity<>(eval, org.springframework.http.HttpStatus.CREATED);
    }

    @RequestMapping(value = { "/getEvaluator3/{id}" }, method = RequestMethod.GET)
    public void getEvaluator3(@PathVariable("id") String id, HttpServletResponse response) throws IOException, URISyntaxException {
        Evaluator eval = evaluators.get(id);
        ServletOutputStream outputStream = response.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(eval);
        outputStream.flush();
    }

    @RequestMapping(value = { "/getEvaluator4/{id}" }, method = RequestMethod.GET)
    public byte[] getEvaluator4(@PathVariable("id") String id) throws IOException {
        Evaluator eval = evaluators.get(id);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(eval);
        objectOutputStream.flush();

        byte[] res = bos.toByteArray();

        bos.close();

        return res;
    }

    @RequestMapping(value = { "/getEvaluator5/{id}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getEvaluator5(@PathVariable("id") String id) throws IOException {

        PMML eval = pmmls.get(id);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(eval);
        objectOutputStream.flush();

        byte[] res = bos.toByteArray();

        bos.close();

        return res;
    }

    @RequestMapping(value = { "/getEvaluator6/{id}" }, method = RequestMethod.GET)
    public void getEvaluator6(@PathVariable("id") String id, HttpServletResponse response) throws IOException, URISyntaxException {
        ServletOutputStream outputStream = response.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        PMML eval = pmmls.get(id);
        objectOutputStream.writeObject(eval);
        outputStream.flush();
    }

    @RequestMapping(value = {"/getIds"}, method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getIds(){
        return evaluators.keySet();
    }

    @RequestMapping(value = { "/getEvaluator7" }, method = RequestMethod.GET)
    public void getEvaluator7(HttpServletResponse response) throws IOException, URISyntaxException {
        Map<String, MiningModelEvaluator> evaluators = this.evaluators;
        ServletOutputStream outputStream = response.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(evaluators);
        outputStream.flush();
    }

    @RequestMapping(value = { "/executeEvaluator/{url}/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public void executeEvaluator(@PathVariable("url") String url, @PathVariable("id") String id) throws IOException, ClassNotFoundException {
        Evaluator evaluator = transformer.executeEvaluation(url + "/" + id);
        evaluator.verify();
    }

    @RequestMapping(value = { "/executePMMLByte/{url}/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public void executePMMLByte(@PathVariable("url") String url, @PathVariable("id") String id) throws IOException, ClassNotFoundException {
        PMML pmml = transformer.executePMMLByte(url + "/" + id);
        System.out.println(pmml);
    }

    @RequestMapping(value = { "/executeEvaluatorByte/{url}/{id}" }, method = RequestMethod.GET)
    @ResponseBody
    public void executeEvaluatorByte(@PathVariable("url") String url, @PathVariable("id") String id) throws IOException, ClassNotFoundException {
        Evaluator evaluator = transformer.executeEvaluatorByte(url + "/" + id);
        System.out.println(evaluator);
    }


    @RequestMapping(value = { "/generateEntity" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TestEntity generate(){
        return testEntityTransformer.generate();
    }

    @RequestMapping(value = { "/generateEntityByte" }, method = RequestMethod.GET)
    @ResponseBody
    public byte[] generate2() throws IOException {
        TestEntity generate = testEntityTransformer.generate();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(generate);
        objectOutputStream.flush();

        byte[] res = bos.toByteArray();

        bos.close();

        return res;
    }

    @RequestMapping(value = { "/deserialize" }, method = RequestMethod.GET)
    @ResponseBody
    public boolean deserialize() throws IOException, ClassNotFoundException {
        TestEntity evaluator = testEntityTransformer.deserialize();
        return evaluator != null;
    }

    @RequestMapping(value = { "/deserialize2" }, method = RequestMethod.GET)
    @ResponseBody
    public boolean deserialize2() throws IOException, ClassNotFoundException {
        TestEntity testEntity = testEntityTransformer.deserialize2();
        System.out.println(testEntity);
        return testEntity != null;
    }


}

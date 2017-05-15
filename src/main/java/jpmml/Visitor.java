package jpmml;

import org.dmg.pmml.Field;
import org.jpmml.model.visitors.FieldResolver;

import java.util.Set;

/**
 * Created by Administrator on 4/19/2017.
 */
public class Visitor {
    public static void main(String[] args) {


        FieldResolver fieldResolver = new FieldResolver();
        Set<Field> fields = fieldResolver.getFields();

        System.out.println(fields);

    }
}

package com.cj.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.Getter;

import javax.xml.namespace.QName;
import java.io.*;
import java.io.StringWriter;

public final class Util {


    @Getter
    private static final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

    /*
   https://stackoverflow.com/questions/11664894/jackson-deserialize-using-generic-class
    */
    public static <T> JavaType getParametricType(Class<T> base, Class<?>... elements) {
        return mapper.getTypeFactory().constructParametricType(base, elements);
    }

    public static <T> T deserialize(Object obj, JavaType javaType) {
        return mapper.convertValue(obj, javaType);
    }

    public static <T> T deserialize(InputStream inputStream, JavaType javaType) throws IOException {
        return mapper.readValue(inputStream, javaType);
    }

    public static <T> T deserialize(InputStream inputStream, Class<T> clazz) throws IOException {
        return mapper.readValue(inputStream, clazz);
    }

    // New
    public static <T> T deserialize(Object obj, Class<T> clazz) {
        return mapper.convertValue(obj, clazz);
    }

    public static String objectToJsonString(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    //New For testing only!!!
    public static InputStream objectToInputStream(Object obj) throws JsonProcessingException {
        byte[] bytes = mapper.writeValueAsBytes(obj);
        return new ByteArrayInputStream(bytes);
    }

    public static <T> String objectToXmlString(Object obj, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        if (obj instanceof JAXBElement<?>) {
            jaxbMarshaller.marshal(obj, sw);
        } else {
            jaxbMarshaller.marshal(wrapWithJAXBelement(obj, clazz), sw);
        }
        return sw.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> JAXBElement<T> wrapWithJAXBelement(Object obj, Class<T> clazz) {
        return new JAXBElement(new QName(clazz.getSimpleName()), clazz, obj);
    }

//    public static AdapterEnum getAdapterFromPackage(String packageName) {
//        return Stream.of(AdapterEnum.values())
//                .filter(ae -> packageName.contains(ae.getValue().toLowerCase()))
//                .findAny().orElse(AdapterEnum.NA);
//    }
}
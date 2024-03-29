package com.cfa.api.corpsupplier.testutil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@UtilityClass   //Lombok
public class ObjectConverter {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();


    public static JsonNode toJsonNodeRemoveField(Object o, String field) {
        return removeField(toJsonNode(o), field);
    }

    public static JsonNode toJsonNode(Object o) {
        try {
            var str = objectMapper.writeValueAsString(o);
            return objectMapper.readTree(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JsonNode removeField(final JsonNode node, String field) {
        return ((ObjectNode) node).remove(List.of(field));
    }

    private String objectToJsonString(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private String objectToXmlString(Object obj, Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(obj, sw);
        return sw.toString();
    }
}
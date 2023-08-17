package com.cfa.sts.integration.common.util;

import com.cfa.sts.integration.common.enums.StatusEnum;
import com.cfa.sts.integration.common.model.platform.MethodMetricData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodMetricDataUtilTest {

    @Test
    @DisplayName("Happy path JSON")
    void happyPathJson() {
        var mmd = new MethodMetricData();
        try (MockedStatic<MethodMetricDataUtil> ignored = Mockito.mockStatic(MethodMetricDataUtil.class, InvocationOnMock::callRealMethod)) {
            MethodMetricDataUtil.finalizeWithJSONPayload(mmd, "hello", StatusEnum.SUCCESS);
            // Expect escaped string so "hello" becomes ""hello""
            assertEquals(7, mmd.getPayloadSize());
            Assertions.assertNotNull(mmd.getEndTs());
        }
    }
    @Test
    @DisplayName("Happy path XML")
    void happyPathXml() {
        var mmd = new MethodMetricData();
        try (MockedStatic<MethodMetricDataUtil> ignored = Mockito.mockStatic(MethodMetricDataUtil.class, InvocationOnMock::callRealMethod)) {
            MethodMetricDataUtil.finalizeWithXMLPayload(mmd,
                    new TestXMLClass("hello"),
                    TestXMLClass.class, StatusEnum.SUCCESS);
            // Expect escaped string so "hello" becomes ""hello""
            assertEquals(76, mmd.getPayloadSize());
            assertEquals("XML", mmd.getPayloadType().getValue());
            Assertions.assertNotNull(mmd.getEndTs());
        }
    }
    @Test
    @DisplayName("Defaults payload when unable to convert JSON object to string")
    void shouldDefaultPayload_whenException_json() {
        var mmd = new MethodMetricData();
        try (MockedStatic<MethodMetricDataUtil> ignored = Mockito.mockStatic(MethodMetricDataUtil.class, invocation -> {
            Method method = invocation.getMethod();
            if ("objectToJsonString".equals(method.getName())) {
                throw new JsonProcessingException("error"){};
            } else {
                return invocation.callRealMethod();
            }
        })) {
            MethodMetricDataUtil.finalizeWithJSONPayload(mmd, new MethodMetricData(), StatusEnum.SUCCESS);
            assertEquals("Error serializing request", mmd.getPayload());
        }
    }

    @Test
    @DisplayName("Defaults payload when unable to convert XML object to string")
    void shouldDefaultPayload_whenException_xml() {
        var mmd = new MethodMetricData();
        //MethodMetricDataUtil is @Utilityclass thus has static methods
        try (MockedStatic<MethodMetricDataUtil> ignored = Mockito.mockStatic(MethodMetricDataUtil.class, invocation -> {
            Method method = invocation.getMethod();
            if ("objectToXmlString".equals(method.getName())) {
                throw new JAXBException("error"){};
            } else {
                return invocation.callRealMethod();
            }
        })) {
            MethodMetricDataUtil.finalizeWithXMLPayload(mmd,
                    new JAXBElement<>(new QName(""), TestXMLClass.class, new TestXMLClass("hello")),
                    TestXMLClass.class, StatusEnum.SUCCESS);
            assertEquals("Error serializing request", mmd.getPayload());
        }
    }



    @XmlRootElement(name = "testXMLClass")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class TestXMLClass {

        protected String val;

        public TestXMLClass(String value) {
            this.val = value;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String value) {
            this.val = value;
        }
    }

}
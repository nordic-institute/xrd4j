/**
 * The MIT License
 * Copyright © 2018 Nordic Institute for Interoperability Solutions (NIIS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xrd4j.common.util;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ConfigurationHelper class.
 *
 * @author Petteri Kivimäki
 */
class ConfigurationHelperTest {

    /**
     * Test parsing client ids from string.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testParseConsumer1() throws XRd4JException {
        String clientId = "FI_PILOT.GOV.0245437-2";
        ConsumerMember consumer = ConfigurationHelper.parseConsumerMember(clientId);

        assertEquals(clientId, consumer.toString());
        assertEquals("FI_PILOT", consumer.getXRoadInstance());
        assertEquals("GOV", consumer.getMemberClass());
        assertEquals("0245437-2", consumer.getMemberCode());
        assertEquals(null, consumer.getSubsystemCode());

        clientId = "FI_PILOT.GOV.0245437-2.ConsumerService";
        consumer = ConfigurationHelper.parseConsumerMember(clientId);
        assertEquals(clientId, consumer.toString());
        assertEquals("FI_PILOT", consumer.getXRoadInstance());
        assertEquals("GOV", consumer.getMemberClass());
        assertEquals("0245437-2", consumer.getMemberCode());
        assertEquals("ConsumerService", consumer.getSubsystemCode());

        clientId = "FI_PILOT.GOV.0245437-2.ConsumerService.getOrganizationList";
        consumer = ConfigurationHelper.parseConsumerMember(clientId);
        assertEquals(null, consumer);

        clientId = "FI_PILOT.GOV.0245437-2.ConsumerService.getOrganizationList.v1";
        consumer = ConfigurationHelper.parseConsumerMember(clientId);
        assertEquals(null, consumer);

        clientId = "FI_PILOT.GOV";
        consumer = ConfigurationHelper.parseConsumerMember(clientId);
        assertEquals(null, consumer);
    }

    /**
     * Test parsing service ids from string.
     *
     * @throws XRd4JException if there's a XRd4J error
     */
    @Test
    void testParseProducer1() throws XRd4JException {
        String serviceId = "FI_PILOT.GOV.0245437-2.ConsumerService.getOrganizationList.v1";
        ProducerMember producer = ConfigurationHelper.parseProducerMember(serviceId);

        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals("ConsumerService", producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("v1", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.ConsumerService2.getOrganizationList.V1";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals("ConsumerService2", producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("V1", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.ConsumerService2.getOrganizationList.V1_0";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals("ConsumerService2", producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("V1_0", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.ConsumerService.getOrganizationList1";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals("ConsumerService", producer.getSubsystemCode());
        assertEquals("getOrganizationList1", producer.getServiceCode());
        assertEquals(null, producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList2";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList2", producer.getServiceCode());
        assertEquals(null, producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.v1";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("v1", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.V11";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("V11", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.1";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("1", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.1_0";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("1_0", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.1-0";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals(null, producer.getSubsystemCode());
        assertEquals("getOrganizationList", producer.getServiceCode());
        assertEquals("1-0", producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV.0245437-2.getOrganizationList.ve1";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(serviceId, producer.toString());
        assertEquals("FI_PILOT", producer.getXRoadInstance());
        assertEquals("GOV", producer.getMemberClass());
        assertEquals("0245437-2", producer.getMemberCode());
        assertEquals("getOrganizationList", producer.getSubsystemCode());
        assertEquals("ve1", producer.getServiceCode());
        assertEquals(null, producer.getServiceVersion());

        serviceId = "FI_PILOT.GOV";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(null, producer);

        serviceId = "FI_PILOT.GOV.0245437-2";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(null, producer);

        serviceId = "FI_PILOT.GOV.0245437-2.subsystem.service.v1.0";
        producer = ConfigurationHelper.parseProducerMember(serviceId);
        assertEquals(null, producer);
    }
}

/*
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
package org.niis.xrd4j.common.member;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.ValidationHelper;

import java.io.Serializable;

/**
 * This class represent X-Road producer member that produces services to
 * X-Road. The services are invoked by ConsumerMembers by sending
 * ServiceRequests. This class identifies the producer member and the
 * subsystem, service and service version that's called by the consumer
 * member.
 *
 * @author Petteri Kivimäki
 */
public class ProducerMember extends AbstractMember implements Serializable {

    /**
     * Namespace prefix of this member
     */
    private String namespacePrefix;
    /**
     * Namespace URL of this member
     */
    private String namespaceUrl;
    /**
     * Code that uniquely identifies a service offered by given SDSB member
     * or subsystem. Optional.
     */
    private String serviceCode;
    /**
     * Version of the service. Optional.
     */
    private String serviceVersion;

    /**
     * Constructs and initializes a new ProducerMember that represents
     * a central service offered by the central servers.
     * @param xRoadInstance identifier of this X-Road instance
     * @param serviceCode unique service code
     * @throws XRd4JException if there's a XRd4J error
     */
    public ProducerMember(String xRoadInstance, String serviceCode) throws XRd4JException {
        super(xRoadInstance);
        this.serviceCode = serviceCode;
        ValidationHelper.validateStrNotNullOrEmpty(serviceCode, Constants.NS_ID_ELEM_SERVICE_CODE);
    }

    /**
     * Constructs and initializes a new ProducerMember.
     * @param xRoadInstance identifier of this X-Road instance
     * @param memberClass type of this member
     * @param memberCode unique member code
     * @param serviceCode code that uniquely identifies a service offered by
     * this member
     * @throws XRd4JException if there's a XRd4J error
     */
    public ProducerMember(String xRoadInstance, String memberClass, String memberCode, String serviceCode) throws XRd4JException {
        super(xRoadInstance, memberClass, memberCode);
        this.serviceCode = serviceCode;
        ValidationHelper.validateStrNotNullOrEmpty(serviceCode, Constants.NS_ID_ELEM_SERVICE_CODE);

    }

    /**
     * Constructs and initializes a new ProducerMember.
     * @param xRoadInstance identifier of this X-Road instance
     * @param memberClass type of this member
     * @param memberCode unique member code
     * @param subsystemCode subsystem code that uniquely identifies a
     * subsystem of this member
     * @param serviceCode code that uniquely identifies a service offered by
     * the given susbsystem of this member
     * @throws XRd4JException if there's a XRd4J error
     */
    public ProducerMember(String xRoadInstance, String memberClass, String memberCode, String subsystemCode, String serviceCode) throws XRd4JException {
        super(xRoadInstance, memberClass, memberCode, subsystemCode);
        this.serviceCode = serviceCode;
        ValidationHelper.validateStrNotNullOrEmpty(serviceCode, Constants.NS_ID_ELEM_SERVICE_CODE);
    }

    /**
     * Constructs and initializes a new ProducerMember.
     * @param xRoadInstance identifier of this X-Road instance
     * @param memberClass type of this member
     * @param memberCode unique member code
     * @param subsystemCode subsystem code that uniquely identifies a
     * subsystem of this member
     * @param serviceCode code that uniquely identifies a service offered by
     * the given susbsystem of this member
     * @param serviceVersion version of the service
     * @throws XRd4JException if there's a XRd4J error
     */
    public ProducerMember(String xRoadInstance, String memberClass, String memberCode, String subsystemCode,
                          String serviceCode, String serviceVersion) throws XRd4JException {
        super(xRoadInstance, memberClass, memberCode, subsystemCode);
        this.serviceCode = serviceCode;
        this.serviceVersion = serviceVersion;
        ValidationHelper.validateStrNotNullOrEmpty(serviceCode, Constants.NS_ID_ELEM_SERVICE_CODE);
        ValidationHelper.validateStrNotNullOrEmpty(serviceVersion, Constants.NS_ID_ELEM_SERVICE_VERSION);
    }

    /**
     * Returns the version number of the service.
     * @return version number of the service
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Sets the version number of the service.
     * @param serviceVersion new value
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * Returns a code that uniquely identifies a service offered by this
     * member.
     * @return code that uniquely identifies a service offered by this
     * member
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Sets the code that uniquely identifies a service offered by this
     * member.
     * @param serviceCode new value
     */
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    /**
     * Returns the namespace prefix of this ProducerMember.
     * @return namespace prefix of this member
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * Sets the namespace prefix of this ProducerMember.
     * @param namespacePrefix new value
     */
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    /**
     * Returns the namespace URL of this ProducerMember.
     * @return namespace URL of this member
     */
    public String getNamespaceUrl() {
        return namespaceUrl;
    }

    /**
     * Sets the namespace URL of this ProducerMember,
     * @param namespaceUrl new value
     */
    public void setNamespaceUrl(String namespaceUrl) {
        this.namespaceUrl = namespaceUrl;
    }

    @Override
    /**
     * Returns a String presentation of this ProducerMember object.
     * @return String presentation of this ProducerMember object
     */
    public String toString() {
        StringBuilder builder = new StringBuilder(super.xRoadInstance).append(".");
        builder.append(super.memberClass != null ? super.memberClass + "." : "");
        builder.append(super.memberCode != null ? super.memberCode + "." : "");
        builder.append(super.subsystemCode != null ? super.subsystemCode + "." : "");
        builder.append(this.getServiceCode());
        builder.append(this.getServiceVersion() != null ? "." + this.getServiceVersion() : "");
        return builder.toString();
    }

    @Override
    /**
     * Indicates whether some other object is "equal to" this ProducerMember.
     * @param o the reference object with which to compare
     * @return true only if the specified object is also an ProducerMember
     * and it has the same id as this ProducerMember
     */
    public boolean equals(Object o) {
        if (o instanceof ProducerMember && this.toString().equals(((ProducerMember) o).toString())) {
            return true;
        }
        return false;
    }

    @Override
    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object
     */
    public int hashCode() {
        return this.toString().hashCode();
    }
}

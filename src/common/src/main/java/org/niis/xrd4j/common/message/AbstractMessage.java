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
package org.niis.xrd4j.common.message;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.member.SecurityServer;
import org.niis.xrd4j.common.util.Constants;
import org.niis.xrd4j.common.util.ValidationHelper;

import jakarta.xml.soap.SOAPMessage;

/**
 * This class represents SOAP header fields that are used in both ServiceRequest
 * and ServiceResponse messages.
 *
 * @author Petteri Kivimäki
 */
public abstract class AbstractMessage {

    /**
     * ConsumerMember that calls a service and acts as a client. Required.
     */
    protected ConsumerMember consumer;
    /**
     * ServiceProvider whose service is being called by the consumer. Required.
     */
    protected ProducerMember producer;
    /**
     * SecurityServer that is hosting the ProducerMember.
     */
    protected SecurityServer securityServer;
    /**
     * Unique identifier for this message. Required.
     */
    protected String id;
    /**
     * User whose action initiated the request. Optional.
     */
    protected String userId;
    /**
     * Identifies received application, issue or document that was the cause of
     * the service request. Optional.
     */
    protected String issue;
    /**
     * X-Road message protocol version. Required.
     */
    private String protocolVersion;
    /**
     * SOAPMessage object representing this message. Required.
     */
    protected SOAPMessage soapMessage;
    /**
     * Possible ErrorMessage related to this message. Value is null, if there's
     * no error.
     */
    protected ErrorMessage errorMessage;

    /**
     * Indicates if "request" and "response" wrappers should be processed.
     * Initializing with default value.
     */
    protected boolean processingWrappers = Constants.DEFAULT_PROCESSING_WRAPPERS;

    /**
     * Field for transferring JSON Web Token over X-Road. Optional.
     */
    private String securityToken;
    /**
     * Type of the security token. Optional.
     */
    private String securityTokenType;

    /**
     * Constructs and initializes a new AbstractMessage object.
     */
    protected AbstractMessage() {
        this.protocolVersion = "4.0";
    }

    /**
     * Constructs and initializes a new AbstractMessage object.
     *
     * @param consumer client that's calling a service
     * @param producer service provider whose service the client is calling
     * @param id unique identifier of the message
     * @throws XRd4JException if there's a XRd4J error
     */
    protected AbstractMessage(ConsumerMember consumer, ProducerMember producer, String id) throws XRd4JException {
        this();
        this.consumer = consumer;
        this.producer = producer;
        this.id = id;
        ValidationHelper.validateNotNull(consumer, "consumer");
        ValidationHelper.validateNotNull(producer, "producer");
        ValidationHelper.validateStrNotNullOrEmpty(id, "id");
    }

    /**
     * Returns the consumer that's calling a service and acting a client.
     *
     * @return ConsumerMember that's acting as a client
     */
    public ConsumerMember getConsumer() {
        return consumer;
    }

    /**
     * Changes the value of the consumer that's calling a service and acting a
     * client.
     *
     * @param consumer new value
     */
    public void setConsumer(ConsumerMember consumer) {
        this.consumer = consumer;
    }

    /**
     * Returns the ServiceProvider whose service is being called by the
     * consumer.
     *
     * @return ProducerMember that's producing the service that's being called
     */
    public ProducerMember getProducer() {
        return producer;
    }

    /**
     * Changes the value of the producer whose service is being called by the
     * consumer.
     *
     * @param producer new value
     */
    public void setProducer(ProducerMember producer) {
        this.producer = producer;
    }

    /**
     * Returns the security server hosting the ProducerMember.
     *
     * @return SecurityServer that's hosting the ProducerMember
     */
    public SecurityServer getSecurityServer() {
        return this.securityServer;
    }

    /**
     * Changes the value of the security server hosting the ProducerMember.
     *
     * @param securityServer new value
     */
    public void setSecurityServer(SecurityServer securityServer) {
        this.securityServer = securityServer;
    }

    /**
     * Returns the unique identifier of the message. The value is set by the
     * ServiceConsumer.
     *
     * @return unique identifier of the message.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the message. The identifier must be set by
     * the ServiceConsumer and the ServiceProducer must not change the value.
     *
     * @param id new universally unique identifier value
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the id of the user whose action initiated the request. The value
     * is set by the ServiceConsumer.
     *
     * @return id of the user whose action initiated the request
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the id of the user whose action initiated the request. The value
     * must be set by the ServiceConsumer and must not be changed by the
     * ServiceProvider.
     *
     * @param userId new value
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the issue that was the cause of the service request.
     *
     * @return issue that was the cause of the service request
     */
    public String getIssue() {
        return issue;
    }

    /**
     * Sets the issue that was the cause of the service request.
     *
     * @param issue new value
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * Returns X-Road message protocol version.
     *
     * @return X-Road message protocol version
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets X-Road message protocol version.
     *
     * @param protocolVersion new value
     */
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Returns the SOAPMessage object related to this message. The SOAPMessage
     * object is always automatically set by request/response serializers and
     * deserializers.
     *
     * @return SOAPMessage object related to this message
     */
    public SOAPMessage getSoapMessage() {
        return soapMessage;
    }

    /**
     * Sets the SOAPMessage object related to this message. The SOAPMessage
     * object is always automatically set by request/response serializers and
     * deserializers and must never be set manually.
     *
     * @param soapMessage new value
     */
    public void setSoapMessage(SOAPMessage soapMessage) {
        this.soapMessage = soapMessage;
    }

    /**
     * Returns the ErrorMessage related to this message. Value is null, if
     * there's no error.
     *
     * @return ErrorMessage related to this message or null
     */
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the ErrorMessage related to this message.
     *
     * @param errorMessage new value
     */
    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns a boolean value that indicates if "request" and "response"
     * wrappers should be processed.
     *
     * @return true or false
     */
    public boolean isProcessingWrappers() {
        return processingWrappers;
    }

    /**
     * Sets the boolean value that indicates if "request" and "response"
     * wrappers should be processed.
     *
     * @param processingWrappers new value
     */
    public void setProcessingWrappers(boolean processingWrappers) {
        this.processingWrappers = processingWrappers;
    }

    /**
     * Returns the security token.
     *
     * @return security token as a String
     */
    public String getSecurityToken() {
        return securityToken;
    }

    /**
     * Sets the security token.
     *
     * @param securityToken new value
     */
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    /**
     * Returns the security token type.
     *
     * @return security token type
     */
    public String getSecurityTokenType() {
        return securityTokenType;
    }

    /**
     * Sets the security token type.
     *
     * @param securityTokenType new value
     */
    public void setSecurityTokenType(String securityTokenType) {
        this.securityTokenType = securityTokenType;
    }

    /**
     * Tells if there's an error related to this message. Returns true if and
     * only if there's an error. Otherwise returns false.
     *
     * @return true if and only if there's an error; otherwise false
     */
    public boolean hasError() {
        if (this.errorMessage == null) {
            return false;
        }
        return true;
    }

    @Override
    /**
     * Returns a String presentation of this ConsumerMember object.
     *
     * @return String presentation of this ConsumerMember object
     */
    public String toString() {
        return this.id;
    }
}

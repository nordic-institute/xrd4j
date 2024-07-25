/**
 * The MIT License
 * Copyright © 2018 Nordic Institute for Interoperability Solutions (NIIS)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p>
 * The MIT License
 * ...
 */
/**
 * The MIT License
 * ...
 */
package org.niis.xrd4j.exampleadapter.mtom;

import com.niis.test.test.messagegen.*;
import org.apache.cxf.headers.Header;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;
import java.util.List;

@WebService(targetNamespace = "http://test.niis.com/test/messagegen", wsdlLocation = "mtomservice.wsdl")
@MTOM
public class MessageMtomGeneratorImpl implements MessageMtomGenerator {
    private static final ObjectFactory FACTORY = new ObjectFactory();
    @Resource
    private WebServiceContext ctx;

    @Override
    public void generateMtom(int payloadSize, int attachmentSize, Integer delay, Holder<DataHandler> attachment, Holder<MessageType> message) throws ErrorResponse {

//        GenerateMtomResponseType response = new GenerateMtomResponseType();

        if (delay != null) {
            if (delay < 0) {
                createError("Delay must be a positive integer");
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // Ignore, but keep status
                Thread.currentThread().interrupt();
            }
        }

        if (attachment.value != null) {
//            response.setAttachment(attachment.value);
            /*
            try (final InputStream attachmentStream = attachment.value.getInputStream()) {
                byte[] buf = new byte[4096];
                while (attachmentStream.read(buf) > 0);
            } catch (IOException e) {
                createError(e.getMessage());
            }
            */
        }

        // Convert bytes to items
        final int items = payloadSize / (2 * "<item>".length() + 1 + 1);

        if (payloadSize > 0 && items == 0) {
            createError("Unable to create payload, try increasing the size");
        }

        if (items > 0) {
            MessageType.Items itemsType = FACTORY.createMessageTypeItems();
            itemsType.getItem().addAll(new LazyCollection(items));
            MessageType messageType = FACTORY.createMessageType();
            messageType.setItems(itemsType);
            message.value = messageType;
        }

        if (attachmentSize > 0) {
            attachment.value = new DataHandler(new AttachmentSource(attachmentSize));
        }

        // Trick to echo all headers back (CXF only)
        final List<Header> headers = (List<Header>) ctx.getMessageContext().get(Header.HEADER_LIST);
        for (Header header : headers) {
            header.setDirection(Header.Direction.DIRECTION_INOUT);
        }

    }

    private void createError(String message) throws ErrorResponse {
        final ErrorResponseType e = new ErrorResponseType();
        e.setError(message);
        throw new ErrorResponse("Error", e);
    }
}

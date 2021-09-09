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

package org.niis.xrd4j.exampleadapter.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.niis.xrd4j.exampleadapter.data.PersonContact.PersonContactType;

/**
 * This class implements a POJO for the "person" complexType defined
 * in the "example.wsdl" file that's located in WEB-INF/classes folder. The name 
 * of the WSDL file and the namespace is configured in the 
 * WEB-INF/classes/xrd-servlet.properties file.
 *
 * Also provides a static method for getting our example dataset.
 *
 * @author Raido Kaju
 */
public class Person {
    private String SSN;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String contactAddress;
    private List<PersonContact> contacts;

    public Person(final String SSN, final String firstName, final String lastName, final LocalDate dateOfBirth, final String contactAddress,
            final List<PersonContact> contacts) {
        this.SSN = SSN;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactAddress = contactAddress;
        this.contacts = contacts;
    }
    
    /**
     * A function to generate our example dataset
     * @return a list of 6 different mock people
     */
    public static List<Person> getExamplePeople() {
        return Arrays.asList(
            new Person("1230", "John", "Smith", LocalDate.of(1965, 1, 20), "Some Street 3, Some City, Some Country", 
                    Arrays.asList(
                        new PersonContact(PersonContactType.PHONE, "Home number", "+123 4567890"),
                        new PersonContact(PersonContactType.EMAIL, "Personal e-mail", "some@email.nil")
                        )
                ),
            new Person("1231", "Jack", "Tree", LocalDate.of(1971, 3, 20), "Some Street 9, Some City, Some Country", 
                    Arrays.asList(
                        new PersonContact(PersonContactType.MOBILE, "Personal number", "+123 456789011")
                        )
                ),
            new Person("1232", "Mary", "Jones", LocalDate.of(1980, 2, 20), "Some Street 7, Some City, Some Country", 
                    null
                ),
            new Person("1233", "Maria", "Johnson", LocalDate.of(1991, 9, 12), "Some Street 30, Some City, Some Country", 
                    Arrays.asList(
                        new PersonContact(PersonContactType.PHONE, "Work number", "+123 4634563")
                        )
                ),
            new Person("1234", "Luke", "Miller", LocalDate.of(1986, 12, 3), "Some Street 87, Some City, Some Country", 
                    Arrays.asList(
                        new PersonContact(PersonContactType.PHONE, "Home number", "+123 13433653"),
                        new PersonContact(PersonContactType.EMAIL, "Personal e-mail", "my@email.nil"),
                        new PersonContact(PersonContactType.MOBILE, "Preferred contact", "+123 365346456")
                        )
                ),
            new Person("1235", "Michelle", "Smith", LocalDate.of(1966, 6, 30), "Some Street 3, Some City, Some Country", 
                    Arrays.asList(
                        new PersonContact(PersonContactType.PHONE, "Home number", "+123 4567890"),
                        new PersonContact(PersonContactType.EMAIL, "Personal e-mail", "other@email.nil")
                        )
                )
            );
    }

    public String getSSN() {
        return SSN;
    }

    public void setSSN(final String sSN) {
        SSN = sSN;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(final String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public List<PersonContact> getContacts() {
        // Since our WSDL definition does not allow this to be missing,
        // we always initialise it as empty in case it is null
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }
        return contacts;
    }

    public void setContacts(final List<PersonContact> contacts) {
        this.contacts = contacts;
    }
}

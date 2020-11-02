package au.com.rma.service;/*
 * Copyright (c) 2020 Richard Allwood
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import au.com.rma.model.dq.DataQualityRequest;
import au.com.rma.model.dq.request.Address;
import au.com.rma.model.dq.request.EmailAddress;
import au.com.rma.model.dq.request.Name;
import au.com.rma.model.dq.request.PhoneNumber;
import au.com.rma.model.dq.response.AddressWithQuality;
import au.com.rma.service.impl.DataQualityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.test.StepVerifier;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class DataQualityServiceTest {
  private DataQualityServiceImpl service = new DataQualityServiceImpl();

  @Test
  public void verifyDataQualityServiceWorksAsExpected() {
    DataQualityRequest request = DataQualityRequest.builder()
        .names(asList(
            Name.builderFor("1234", "john", "smith").build(),
            Name.builderFor("5678", "andrew", "jones").build()))
        .addresses(asList(
            Address.builderFor("Work", "123 albert street").build(),
            Address.builderFor("Home", "30 queen street")
                .suburb("brisbane")
                .build()))
        .phoneNumbers(asList(
            new PhoneNumber("Mobile", "0400123456"),
            new PhoneNumber("Work", "0800123456"),
            new PhoneNumber("Work2", "0800123457"),
            new PhoneNumber("Home", "0712340010"),
            new PhoneNumber("Home2", "0712340011")))
        .emailAddresses(asList(
            new EmailAddress("Work", "test1@test.com"),
            new EmailAddress("Home", "test2@test.com")))
        .build();

    StepVerifier.create(service.standardise(request))
        .consumeNextWith(r -> {
          log.info("DataQualityResponse: {}", r);

          assertThat(r.getNames().size(), is(2));
          assertThat(r.findName("1234").fullName(), is("JOHN SMITH"));
          assertThat(r.findName("5678").fullName(), is("ANDREW JONES"));

          assertThat(r.getAddresses().size(), is(2));
          AddressWithQuality address = r.findAddress("Work");
          assertThat(address.getLine1(), is("123 ALBERT STREET"));
          assertThat(address.getSuburb(), is("BRISBANE CBD"));
          assertThat(address.getState(), is("QLD"));
          assertThat(address.getPostcode(), is("4000"));
          assertThat(address.getCountry(), is("AUSTRALIA"));
          address = r.findAddress("Home");
          assertThat(address.getLine1(), is("30 QUEEN STREET"));
          assertThat(address.getSuburb(), is("BRISBANE"));
          assertThat(address.getState(), is("QLD"));
          assertThat(address.getPostcode(), is("4000"));
          assertThat(address.getCountry(), is("AUSTRALIA"));

          assertThat(r.getEmailAddesses().size(), is(2));
          assertThat(r.findEmailAddress("Work").getEmailAddress(), is("TEST1@TEST.COM"));
          assertThat(r.findEmailAddress("Home").getEmailAddress(), is("TEST2@TEST.COM"));

          assertThat(r.getPhoneNumbers().size(), is(5));
          assertThat(r.findPhoneNumber("Home").getPhoneNumber(), is("+61712340010"));
          assertThat(r.findPhoneNumber("Home2").getPhoneNumber(), is("+61712340011"));
          assertThat(r.findPhoneNumber("Work").getPhoneNumber(), is("+61800123456"));
          assertThat(r.findPhoneNumber("Work2").getPhoneNumber(), is("+61800123457"));
          assertThat(r.findPhoneNumber("Mobile").getPhoneNumber(), is("+61400123456"));
        })
        .verifyComplete();
  }
}
/*
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
package au.com.rma.service.impl.standardisation;

import au.com.rma.model.dq.DataQualityResponse;
import au.com.rma.model.dq.request.Address;
import au.com.rma.model.dq.request.EmailAddress;
import au.com.rma.model.dq.response.AddressWithQuality;
import au.com.rma.model.dq.response.EmailAddressWithQuality;
import au.com.rma.model.harmony.HarmonyAddress;
import au.com.rma.model.harmony.HarmonyRequest;
import au.com.rma.model.harmony.HarmonyResponse;
import au.com.rma.model.harmony.ValueWithQuality;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressStandardisation implements StandardisationStrategy {

  private final Address address;

  @Override
  public StandardisationType getType() {
    return StandardisationType.ADDRESS;
  }

  @Override
  public void populateHarmonyRequest(HarmonyRequest harmonyRequest) {
    harmonyRequest.setAddress(HarmonyAddress.builder()
        .line1(address.getLine1())
        .line2(address.getLine2())
        .suburb(address.getSuburb())
        .state(address.getState())
        .postcode(address.getPostcode())
        .country(address.getCountry())
        .build());
  }

  @Override
  public void processHarmonyResponse(HarmonyResponse harmonyResponse, DataQualityResponse response) {
    ValueWithQuality<HarmonyAddress> a = harmonyResponse.getAddress();
    HarmonyAddress address = a.getValue();

    response.addAddress(AddressWithQuality.builder()
        .type(this.address.getType())
        .line1(address.getLine1())
        .line2(address.getLine2())
        .suburb(address.getSuburb())
        .state(address.getState())
        .postcode(address.getPostcode())
        .country(address.getCountry())
        .dpid(address.getDpid())
        .dataQuality(fromDataQuality(a.getQuality()))
        .build());
  }
}

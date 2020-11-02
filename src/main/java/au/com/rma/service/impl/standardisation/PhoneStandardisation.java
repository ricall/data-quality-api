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
import au.com.rma.model.dq.request.PhoneNumber;
import au.com.rma.model.dq.response.PhoneNumberWithQuality;
import au.com.rma.model.harmony.HarmonyRequest;
import au.com.rma.model.harmony.HarmonyResponse;
import au.com.rma.model.harmony.ValueWithQuality;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PhoneStandardisation implements StandardisationStrategy {

  private final PhoneNumber phone;

  @Override
  public StandardisationType getType() {
    return StandardisationType.PHONE_NUMBER;
  }

  @Override
  public void populateHarmonyRequest(HarmonyRequest harmonyRequest) {
    harmonyRequest.setPhoneNumber(phone.getPhoneNumber());
  }

  @Override
  public void processHarmonyResponse(HarmonyResponse harmonyResponse, DataQualityResponse response) {
    ValueWithQuality<String> phoneNumber = harmonyResponse.getPhoneNumber();
    response.addPhoneNumber(PhoneNumberWithQuality.builder()
        .type(phone.getType())
        .phoneNumber(phoneNumber.getValue())
        .dataQuality(fromDataQuality(phoneNumber.getQuality()))
        .build());
  }
}

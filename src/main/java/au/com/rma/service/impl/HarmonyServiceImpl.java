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
package au.com.rma.service.impl;

import au.com.rma.model.harmony.HarmonyAddress;
import au.com.rma.model.harmony.HarmonyRequest;
import au.com.rma.model.harmony.HarmonyResponse;
import au.com.rma.model.harmony.ValueWithQuality;
import au.com.rma.model.harmony.ValueWithQuality.Quality;
import au.com.rma.service.HarmonyService;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.upperCase;

public class HarmonyServiceImpl implements HarmonyService {
  private Random random = new Random();

  @Override
  public Mono<HarmonyResponse> queryHarmony(HarmonyRequest request) {
    HarmonyResponse response = HarmonyResponse.builder()
        .names(getParseNames(request.getName()))
        .address(parseAddress(request.getAddress()))
        .emailAddress(parseEmail(request.getEmailAddress()))
        .phoneNumber(parsePhoneNumber(request.getPhoneNumber()))
        .build();

    return Mono.just(response)
        .delayElement(Duration.of(100 + random.nextInt(100), ChronoUnit.MILLIS));
//        .delayElement(Duration.of(100, ChronoUnit.MILLIS));
  }

  private ValueWithQuality<HarmonyAddress> parseAddress(HarmonyAddress address) {
    if (address == null) {
      return null;
    }
    return withQuality(HarmonyAddress.builder()
        .line1(upperCase(address.getLine1()))
        .line2(upperCase(address.getLine2()))
        .suburb(upperCase(address.getSuburb()))
        .state(upperCase(address.getState()))
        .postcode(upperCase(address.getPostcode()))
        .country(upperCase(address.getCountry()))
        .dpid(UUID.randomUUID().toString())
        .build());
  }

  private ValueWithQuality<String> parsePhoneNumber(String phoneNumber) {
    if (phoneNumber == null) {
      return null;
    }
    if (phoneNumber.startsWith("0")) {
      return withQuality("+61" + phoneNumber.substring(1));
    }
    return withQuality(phoneNumber);
  }

  private ValueWithQuality<String> parseEmail(String emailAddress) {
    if (emailAddress == null) {
      return null;
    }
    return withQuality(upperCase(emailAddress));
  }

  private List<ValueWithQuality<String>> getParseNames(String name) {
    if (name == null) {
      return Collections.emptyList();
    }
    return stream(upperCase(name).split(" AND "))
        .map(StringUtils::trimToNull)
        .filter(Objects::nonNull)
        .map(this::withQuality)
        .collect(Collectors.toList());
  }

  private <T> ValueWithQuality<T> withQuality(T value) {
    return ValueWithQuality.<T>builder()
        .value(value)
        .quality(randomQuality())
        .build();
  }

  private Quality randomQuality() {
    return Quality.values()[random.nextInt(3)];
  }
}

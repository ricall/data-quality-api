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
package au.com.rma.service;

import au.com.rma.model.harmony.HarmonyRequest;
import au.com.rma.service.impl.HarmonyServiceImpl;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HarmonyServiceTest {

  private HarmonyServiceImpl service = new HarmonyServiceImpl();

  @Test
  public void verifyHarmonyServiceWorksAsExpected() {
    HarmonyRequest request = HarmonyRequest.builder()
        .name("john smith and mary smith")
        .emailAddress("test@test.com")
        .phoneNumber("0400123456")
        .build();

    StepVerifier
        .withVirtualTime(() -> service.queryHarmony(request))
        .expectSubscription()
        .thenAwait(Duration.ofMillis(1000))
        .consumeNextWith(response -> {
          assertThat(response.getNames().size(), is(2));
          assertThat(response.getNames().get(0).getValue(), is("JOHN SMITH"));
          assertThat(response.getNames().get(1).getValue(), is("MARY SMITH"));
          assertThat(response.getEmailAddress().getValue(), is("TEST@TEST.COM"));
          assertThat(response.getPhoneNumber().getValue(), is("+61400123456"));
        })
        .verifyComplete();
  }
}
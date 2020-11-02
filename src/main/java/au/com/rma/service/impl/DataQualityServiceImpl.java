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

import au.com.rma.model.dq.DataQualityRequest;
import au.com.rma.model.dq.DataQualityResponse;
import au.com.rma.model.harmony.HarmonyRequest;
import au.com.rma.model.harmony.HarmonyResponse;
import au.com.rma.service.DataQualityService;
import au.com.rma.service.impl.standardisation.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static au.com.rma.service.impl.standardisation.PartitionFactory.partitionsFor;
import static java.util.Arrays.asList;

@Slf4j
public class DataQualityServiceImpl implements DataQualityService {
  private HarmonyServiceImpl service = new HarmonyServiceImpl();

  @Override
  public Mono<DataQualityResponse> standardise(DataQualityRequest request) {
    DataQualityResponse response = new DataQualityResponse();

    log.info("DataQualityRequest: {}", request);
    return Flux.fromIterable(partitionsFor(createProcessorsFor(request)))
        .flatMap(partition -> queryHarmony(partition, response))
        .then(Mono.just(response));
  }

  private List<StandardisationStrategy> createProcessorsFor(DataQualityRequest request) {
    return asList(
            request.getNames().stream().map(NameStandardisation::new),
            request.getAddresses().stream().map(AddressStandardisation::new),
            request.getEmailAddresses().stream().map(EmailStandardisation::new),
            request.getPhoneNumbers().stream().map(PhoneStandardisation::new)).stream()
        .reduce(Stream.empty(), Stream::concat)
        .collect(Collectors.toList());
  }

  private Mono<HarmonyResponse> queryHarmony(List<StandardisationStrategy> processors, DataQualityResponse response) {
    return service.queryHarmony(createRequest(processors))
        .publishOn(Schedulers.single())
        .doOnNext(r -> {
          log.info("Received harmony response: {}", r);
          processors.stream().forEach(p -> p.processHarmonyResponse(r, response));
        });
  }

  private HarmonyRequest createRequest(List<StandardisationStrategy> processors) {
    HarmonyRequest request = new HarmonyRequest();
    processors.stream().forEach(p -> p.populateHarmonyRequest(request));

    log.info("Querying harmony: {}", request);
    return request;
  }

}

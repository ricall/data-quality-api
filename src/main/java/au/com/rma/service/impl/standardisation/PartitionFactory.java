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

import au.com.rma.service.impl.standardisation.StandardisationStrategy.StandardisationType;

import java.util.ArrayList;
import java.util.List;

public class PartitionFactory {

  private List<List<StandardisationStrategy>> partitions = new ArrayList<>();

  private List<StandardisationStrategy> partitionForType(StandardisationType type) {
    List<StandardisationStrategy> partition = partitions.stream()
        .filter(p -> !partitionHasType(p, type))
        .findFirst()
        .orElse(null);
    if (partition == null) {
      partition = new ArrayList<>();
      partitions.add(partition);
    }
    return partition;
  }

  private boolean partitionHasType(List<StandardisationStrategy> partition, StandardisationType type) {
    return partition.stream()
        .map(StandardisationStrategy::getType)
        .anyMatch(t -> t.equals(type));
  }

  private void allocatePartition(StandardisationStrategy processor) {
      partitionForType(processor.getType()).add(processor);
  }

  private List<List<StandardisationStrategy>> allocatePartitions(List<StandardisationStrategy> processors) {
    processors.forEach(this::allocatePartition);

    return partitions;
  }

  public static List<List<StandardisationStrategy>> partitionsFor(List<StandardisationStrategy> processors) {
    return new PartitionFactory().allocatePartitions(processors);
  }
}

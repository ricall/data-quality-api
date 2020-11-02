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
package au.com.rma.model.dq;

import au.com.rma.model.dq.response.AddressWithQuality;
import au.com.rma.model.dq.response.EmailAddressWithQuality;
import au.com.rma.model.dq.response.NameWithQuality;
import au.com.rma.model.dq.response.PhoneNumberWithQuality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityResponse {
  @Builder.Default
  private List<NameWithQuality> names = new ArrayList<>();
  @Builder.Default
  private List<AddressWithQuality> addresses = new ArrayList<>();
  @Builder.Default
  private List<EmailAddressWithQuality> emailAddesses = new ArrayList<>();
  @Builder.Default
  private List<PhoneNumberWithQuality> phoneNumbers = new ArrayList<>();

  public void addNames(Collection<NameWithQuality> names) {
    this.names.addAll(names);
  }

  public NameWithQuality findName(String identifier) {
    return names.stream()
        .filter(n -> identifier.equals(n.getIdentifier()))
        .findFirst()
        .orElse(null);
  }

  public void addAddress(AddressWithQuality address) {
    this.addresses.add(address);
  }

  public AddressWithQuality findAddress(String type) {
    return addresses.stream()
        .filter(a -> type.equals(a.getType()))
        .findFirst()
        .orElse(null);
  }

  public void addEmailAddress(EmailAddressWithQuality emailAddress) {
    emailAddesses.add(emailAddress);
  }

  public EmailAddressWithQuality findEmailAddress(String type) {
    return emailAddesses.stream()
        .filter(e -> type.equals(e.getType()))
        .findFirst()
        .orElse(null);
  }

  public void addPhoneNumber(PhoneNumberWithQuality phoneNumber) {
    phoneNumbers.add(phoneNumber);
  }

  public PhoneNumberWithQuality findPhoneNumber(String type) {
    return phoneNumbers.stream()
        .filter(p -> type.equals(p.getType()))
        .findFirst()
        .orElse(null);
  }
}

package dk.fakeinfo.controller;

import dk.fakeinfo.model.AddressResponse;
import dk.fakeinfo.model.CprResponse;
import dk.fakeinfo.model.FakePerson;
import dk.fakeinfo.model.NameGenderDobResponse;
import dk.fakeinfo.model.NameGenderResponse;
import dk.fakeinfo.model.PhoneResponse;
import dk.fakeinfo.service.FakePersonService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeInfoController {

    private final FakePersonService fakePersonService;

    public FakeInfoController(FakePersonService fakePersonService) {
        this.fakePersonService = fakePersonService;
    }

    @GetMapping("/cpr")
    public CprResponse getCpr() {
        return new CprResponse(fakePersonService.getFakeCpr());
    }

    @GetMapping("/name-gender")
    public NameGenderResponse getNameGender() {
        return fakePersonService.getNameGender();
    }

    @GetMapping("/name-gender-dob")
    public NameGenderDobResponse getNameGenderDob() {
        return fakePersonService.getNameGenderDob();
    }

    @GetMapping("/cpr-name-gender")
    public FakePerson getCprNameGender() {
        return fakePersonService.getCprNameGender();
    }

    @GetMapping("/cpr-name-gender-dob")
    public FakePerson getCprNameGenderDob() {
        return fakePersonService.getCprNameGenderDob();
    }

    @GetMapping("/address")
    public AddressResponse getAddress() {
        return new AddressResponse(fakePersonService.getAddress());
    }

    @GetMapping("/phone")
    public PhoneResponse getPhone() {
        return new PhoneResponse(fakePersonService.getPhoneNumber());
    }

    @GetMapping("/person")
    public Object getPerson(@RequestParam(required = false) Integer n) {
        if (n == null) {
            return fakePersonService.getFakePerson();
        }
        List<FakePerson> people = fakePersonService.getFakePersons(n);
        return people;
    }
}

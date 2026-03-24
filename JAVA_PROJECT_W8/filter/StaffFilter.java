package filter;

import user.Person;

// PDF Section 12: Functional Interface — exactly ONE abstract method
@FunctionalInterface
public interface StaffFilter {
    boolean test(Person staff);
}
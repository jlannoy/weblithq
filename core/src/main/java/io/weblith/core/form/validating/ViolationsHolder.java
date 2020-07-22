package io.weblith.core.form.validating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ViolationsHolder {

    private final Map<String, List<Violation>> violations = new HashMap<String, List<Violation>>();

    public boolean hasViolations() {
        return !this.violations.isEmpty();
    }

    public boolean hasViolation(String paramName) {
        return this.violations.containsKey(paramName);
    }

    public List<Violation> getViolations() {
        if (violations == null || violations.isEmpty()) {
            return new ArrayList<>();
        }
        return this.violations.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public List<Violation> getViolations(String paramName) {
        return this.violations.getOrDefault(paramName, new ArrayList<>());
    }

    public void addViolation(Violation violation) {
        if (!this.violations.containsKey(violation.getFieldKey())) {
            this.violations.put(violation.getFieldKey(), new ArrayList<>());
        }
        this.violations.get(violation.getFieldKey()).add(violation);
    }

}

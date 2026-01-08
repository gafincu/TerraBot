package main.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Reporter extends User {
    // Nu are câmpuri extra, dar e necesar pentru Jackson
}
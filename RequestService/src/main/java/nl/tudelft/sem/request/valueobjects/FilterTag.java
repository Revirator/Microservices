package nl.tudelft.sem.request.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterTag {
    private String typeOfFiltering;
    private int startOfTheInterval;
    private int endOfTheInterval;
}

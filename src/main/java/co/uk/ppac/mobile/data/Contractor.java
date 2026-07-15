package co.uk.ppac.mobile.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * One contractor loaded from the UAT contractors dump
 * ({@code input/Contractor/uat-db.contractors.json}). Field names mirror the
 * source JSON 1:1 so the file stays the single source of truth - adding a
 * contractor, site or subcontractor to the DB needs no code change.
 *
 * @param contractorName   display name shown in the suggestion (e.g. "Mace Construct")
 * @param contractorPrefix prefix typed in New Check step 1 (e.g. "macec")
 * @param siteLocations    sites selectable in step 2
 * @param subContractors   subcontractors selectable in step 2 (may be empty)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Contractor(
        String contractorName,
        String contractorPrefix,
        List<String> siteLocations,
        List<String> subContractors) {

    /** First site - the deterministic pick used by data-driven New Check tests. */
    public String firstSite() {
        return siteLocations.get(0);
    }

    /** First subcontractor - the deterministic pick used by data-driven New Check tests. */
    public String firstSubcontractor() {
        return subContractors.get(0);
    }

    /** True when this contractor can drive the full flow (needs both a site and a subcontractor). */
    public boolean isFlowReady() {
        return siteLocations != null && !siteLocations.isEmpty()
                && subContractors != null && !subContractors.isEmpty();
    }
}

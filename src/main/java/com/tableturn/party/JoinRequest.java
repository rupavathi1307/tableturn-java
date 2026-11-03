package com.tableturn.party;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** POST /waitlist: who is waiting, how many they are, and a phone number if they give one (E.164). */
public record JoinRequest(@NotBlank @Size(max = 60) String name,
                          @NotNull @Min(1) @Max(20) Integer covers,
                          @Pattern(regexp = "\\+[1-9]\\d{1,14}") String phone) {
}
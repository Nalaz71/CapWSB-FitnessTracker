package pl.wsb.fitnesstracker.user.internal;

import jakarta.annotation.Nullable;

record UserSimpleDto(@Nullable Long id, String firstName, String lastName) {
}


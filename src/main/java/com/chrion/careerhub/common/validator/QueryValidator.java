package com.chrion.careerhub.common.validator;

import com.chrion.careerhub.common.exception.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class QueryValidator {

    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new CustomException(
                    "Page cannot be negative",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (size < 1 || size > 100) {
            throw new CustomException(
                    "Page size must be between 1 and 100",
                    HttpStatus.BAD_REQUEST
            );
        }
    }


    public void validateDirection(String direction) {
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new CustomException(
                    "Sort direction must be asc or desc",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
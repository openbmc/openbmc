/*
* Copyright OpenEmbedded Contributors
*
* SPDX-License-Identifier: MIT
*/

#include "cpp-example-lib.hpp"

#include <iostream>

/* This is for creating a failing test for testing the test infrastructure */
#ifndef FAIL_COMPARISON_STR
#define FAIL_COMPARISON_STR ""
#endif

int main() {
    auto cpp_example = CppExample();
    auto ret_string = cpp_example.get_string();
    if(0 == ret_string.compare(CppExample::test_string + FAIL_COMPARISON_STR)) {
        std::cout << "PASS: " << ret_string << " = " << CppExample::test_string << std::endl;
    } else {
        std::cout << "FAIL: " << ret_string << " != " << CppExample::test_string << std::endl;
        return 1;
    }
}

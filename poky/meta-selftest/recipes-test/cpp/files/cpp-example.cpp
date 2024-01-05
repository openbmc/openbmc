/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include "cpp-example-lib.hpp"

#include <iostream>

int main()
{
    auto cpp_example = CppExample();
    std::cout << "C++ example linking " << cpp_example.get_string() << std::endl;
    std::cout << "Linking json-c version " << cpp_example.get_json_c_version() << std::endl;
    cpp_example.print_json();
    return 0;
}

/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#pragma once

#include <iostream>
#include <string>
#include "config.h"

struct CppExample
{
    // NOLINTNEXTLINE(bugprone-throwing-static-initialization) -- exercises debugger handling of a lazy-init static member, see devtool ide-sdk tests
    inline static const std::string test_string = "cpp-example-lib Magic: 123456789";

    /* Header-only function, to exercise breakpoint resolution against
     * header-only debug info. volatile prevents compiler optimization from
     * eliminating the function body, ensuring a concrete code location exists
     * for debugger breakpoints. */
    static int scale_number(int n)
    {
        volatile int scaled = n * 7;
        std::cout << "scale_number(" << n << ") = " << scaled << std::endl;
        return scaled;
    }

    /* Retrieve a constant string */
    const std::string &get_string();
    /* Retrieve a constant string from a library */
    const char *get_json_c_version();
    /* Call a more advanced function from a library */
    void print_json();
    /* Read hello world message from config file */
    std::string read_config_message(const std::string &config_path = EXAMPLE_CONFIG_PATH);
};

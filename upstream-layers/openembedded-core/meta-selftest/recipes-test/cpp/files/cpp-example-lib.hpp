/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#pragma once

#include <string>
#include "config.h"

struct CppExample
{
    inline static const std::string test_string = "cpp-example-lib Magic: 123456789";

    /* Retrieve a constant string */
    const std::string &get_string();
    /* Retrieve a constant string from a library */
    const char *get_json_c_version();
    /* Call a more advanced function from a library */
    void print_json();
    /* Read hello world message from config file */
    std::string read_config_message(const std::string &config_path = EXAMPLE_CONFIG_PATH);
};

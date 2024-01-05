/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include <iostream>
#include <string>
#include <json-c/json.h>
#include "cpp-example-lib.hpp"

const std::string &CppExample::get_string()
{
    return test_string;
}

const char *CppExample::get_json_c_version()
{
    return json_c_version();
}

void CppExample::print_json()
{
    struct json_object *jobj;
    const int flag = JSON_C_TO_STRING_SPACED | JSON_C_TO_STRING_PRETTY;

    jobj = json_object_new_object();
    json_object_object_add(jobj, "test_string", json_object_new_string(test_string.c_str()));

    std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;

    json_object_put(jobj); // Delete the json object
}

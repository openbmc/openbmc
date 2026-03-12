/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include <iostream>
#include <string>
#include <fstream>
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

std::string CppExample::read_config_message(const std::string &config_path)
{
    std::ifstream config_file(config_path);
    if (!config_file.is_open()) {
        return "Error: Could not open config file: " + config_path;
    }

    std::string config_content((std::istreambuf_iterator<char>(config_file)),
                               std::istreambuf_iterator<char>());
    config_file.close();

    struct json_object *jobj = json_tokener_parse(config_content.c_str());
    if (!jobj) {
        return "Error: Invalid JSON in config file";
    }

    struct json_object *message_obj;
    if (json_object_object_get_ex(jobj, "hello_world_message", &message_obj)) {
        const char *message = json_object_get_string(message_obj);
        std::string result = message ? message : "Error: Invalid message format";
        json_object_put(jobj);
        return result;
    }

    json_object_put(jobj);
    return "Error: 'hello_world_message' not found in config file";
}

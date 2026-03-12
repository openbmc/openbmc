/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include "cpp-example-lib.hpp"

#include <iostream>
#include <unistd.h>
#include <string>
#include <vector>

int main(int argc, char* argv[])
{
    bool endless_mode = false;

    // Parse command line arguments
    for (int i = 1; i < argc; i++) {
        if (std::string(argv[i]) == "--endless") {
            endless_mode = true;
        } else if (std::string(argv[i]) == "--help" || std::string(argv[i]) == "-h") {
            std::cout << "Usage: " << argv[0] << " [OPTIONS]" << std::endl;
            std::cout << "Options:" << std::endl;
            std::cout << "  --endless    Run in endless loop mode (for service)" << std::endl;
            std::cout << "  --help, -h   Show this help message" << std::endl;
            return 0;
        }
    }

    auto cpp_example = CppExample();

    if (endless_mode) {
        std::cout << "Starting cpp-example service in endless mode..." << std::endl;
    } else {
        std::cout << "Running cpp-example once..." << std::endl;
    }

    std::cout << "C++ example linking " << cpp_example.get_string() << std::endl;
    std::cout << "Linking json-c version " << cpp_example.get_json_c_version() << std::endl;
    cpp_example.print_json();

    do {
        // Read and print message from config file
        std::string config_message = cpp_example.read_config_message();
        std::cout << "Config file message: " << config_message << std::endl;

        if (endless_mode) {
            // Sleep for 1 second
            sleep(1);
        }
    } while (endless_mode);

    // Example: Demonstrate std::vector traversal for debugger inspection
    std::vector<int> numbers = {1, 2, 3};
    std::cout << "Traversing std::vector<int> numbers:" << std::endl;
    for (size_t i = 0; i < numbers.size(); ++i) {
        std::cout << "numbers[" << i << "] = " << numbers[i] << std::endl;
    }

    return 0;
}

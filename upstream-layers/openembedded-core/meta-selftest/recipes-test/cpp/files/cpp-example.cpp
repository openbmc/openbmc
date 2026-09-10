/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include "cpp-example-lib.hpp"

#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <string>
#include <vector>

#ifndef WITH_SYSTEMD
#include <sys/types.h>
#include <syslog.h>

#include "daemonize.hpp"
#endif

namespace {

#ifndef WITH_SYSTEMD
bool g_use_syslog = false;
#endif

// Prints an informational message. Under systemd, stdout is already captured
// by the journal. Otherwise, use stdout until daemonized (stdio is then
// redirected to /dev/null), and syslog afterwards so messages aren't lost.
void log_info(const std::string &msg)
{
#ifndef WITH_SYSTEMD
    if (g_use_syslog) {
        syslog(LOG_INFO, "%s", msg.c_str());
        return;
    }
#endif
    std::cout << msg << std::endl;
}

} // namespace

int main(int argc, char *argv[])
{
    bool endless_mode = false;
#ifndef WITH_SYSTEMD
    bool daemonize_mode = false;
    std::string pidfile_path;
    bool have_uid = false;
    bool have_gid = false;
    uid_t target_uid = 0;
    gid_t target_gid = 0;
#endif

    // Parse command line arguments
    for (int i = 1; i < argc; i++) {
        std::string arg = argv[i];
        if (arg == "--endless") {
            endless_mode = true;
#ifndef WITH_SYSTEMD
        } else if (arg == "--daemonize") {
            daemonize_mode = true;
        } else if (arg == "--pidfile" && i + 1 < argc) {
            pidfile_path = argv[++i];
        } else if (arg == "--uid" && i + 1 < argc) {
            target_uid = static_cast<uid_t>(std::strtoul(argv[++i], nullptr, 10));
            have_uid = true;
        } else if (arg == "--gid" && i + 1 < argc) {
            target_gid = static_cast<gid_t>(std::strtoul(argv[++i], nullptr, 10));
            have_gid = true;
#endif
        } else if (arg == "--help" || arg == "-h") {
            std::cout << "Usage: " << argv[0] << " [OPTIONS]" << std::endl;
            std::cout << "Options:" << std::endl;
            std::cout << "  --endless          Run in endless loop mode (for service)" << std::endl;
#ifndef WITH_SYSTEMD
            std::cout << "  --daemonize        Detach from the controlling terminal" << std::endl;
            std::cout << "  --pidfile <path>   Write the daemon's PID to <path>" << std::endl;
            std::cout << "  --uid <uid>        Drop root privileges to this user ID" << std::endl;
            std::cout << "  --gid <gid>        Drop root privileges to this group ID" << std::endl;
#endif
            std::cout << "  --help, -h         Show this help message" << std::endl;
            return 0;
        }
    }

#ifndef WITH_SYSTEMD
    if (daemonize_mode) {
        daemonize();
        openlog(argv[0], LOG_PID, LOG_DAEMON);
        g_use_syslog = true;
    }

    if (!pidfile_path.empty()) {
        write_pidfile(pidfile_path);
    }

    // Drop privileges after daemonizing/writing the pidfile (both may need
    // root, e.g. to create files under /var/run), but before doing any work.
    drop_privileges(have_gid, target_gid, have_uid, target_uid);
#endif

    auto cpp_example = CppExample();

    if (endless_mode) {
        log_info("Starting cpp-example service in endless mode...");
    } else {
        log_info("Running cpp-example once...");
    }

    log_info("C++ example linking " + cpp_example.get_string());
    log_info(std::string("Linking json-c version ") + cpp_example.get_json_c_version());
    cpp_example.print_json();

    // NOLINTNEXTLINE(bugprone-infinite-loop) -- intentionally endless when requested; stopped externally (e.g. systemd stop/SIGTERM)
    do {
        // Read and print message from config file
        std::string config_message = cpp_example.read_config_message();
        log_info("Config file message: " + config_message);

        if (endless_mode) {
            // Sleep for 1 second
            sleep(1);
        }
    } while (endless_mode);
    volatile int n1 = 1, n2 = 2, n3 = 3;
    // Example: Demonstrate std::vector traversal for debugger inspection
    std::vector<int> numbers = {n1, n2, n3};
    std::cout << "Traversing std::vector<int> numbers:" << std::endl;
    for (size_t i = 0; i < numbers.size(); ++i) {
        std::cout << "numbers[" << i << "] = " << numbers[i] << std::endl;
    }

    // Pass numbers elements as the argument so the compiler cannot eliminate
    // the vector; 1+2+3 == 6, so the scale_number(n) check is unchanged.
    CppExample::scale_number(numbers[0] + numbers[1] + numbers[2]);

    return 0;
}

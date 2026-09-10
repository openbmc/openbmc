/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#include "daemonize.hpp"

#include <cstdlib>
#include <fcntl.h>
#include <fstream>
#include <grp.h>
#include <unistd.h>

void daemonize()
{
    pid_t pid = fork();
    if (pid < 0) {
        std::perror("fork");
        std::exit(1);
    }
    if (pid > 0) {
        _exit(0);
    }
    setsid();

    // setsid() only drops the controlling-terminal association; stdio still
    // points at the console, so redirect it or an --endless service keeps
    // the console open/busy with its output forever.
    int null_fd = open("/dev/null", O_RDWR);
    if (null_fd >= 0) {
        dup2(null_fd, STDIN_FILENO);
        dup2(null_fd, STDOUT_FILENO);
        dup2(null_fd, STDERR_FILENO);
        if (null_fd > STDERR_FILENO) {
            close(null_fd);
        }
    }
}

void write_pidfile(const std::string &path)
{
    std::ofstream pidfile(path, std::ios::trunc);
    pidfile << getpid() << std::endl;
}

void drop_privileges(bool have_gid, gid_t gid, bool have_uid, uid_t uid)
{
    if (have_gid && (setgroups(0, nullptr) != 0 || setgid(gid) != 0)) {
        std::perror("setgid");
        std::exit(1);
    }
    if (have_uid && setuid(uid) != 0) {
        std::perror("setuid");
        std::exit(1);
    }
}

/*
 * Copyright OpenEmbedded Contributors
 *
 * SPDX-License-Identifier: MIT
 */

#pragma once

// Legacy SysV-init daemonizing helpers. Not needed (and not built) when
// WITH_SYSTEMD is set, since systemd already daemonizes, drops privileges,
// and tracks the pid itself.

#include <string>
#include <sys/types.h>

// Detaches from the controlling terminal. Uses a single fork (not the classic
// double-fork) so this process becomes its own session AND process-group
// leader (pgid == pid); a second fork would hand it off to a child with a
// stale pgid, which breaks gdbserver's SIGINT-to-process-group interrupt.
void daemonize();

// Writes the current process's pid to the given path.
void write_pidfile(const std::string &path);

// Permanently drops from root to the given group/user. Must run gid before
// uid: once uid is dropped, the process no longer has permission to setgid.
void drop_privileges(bool have_gid, gid_t gid, bool have_uid, uid_t uid);

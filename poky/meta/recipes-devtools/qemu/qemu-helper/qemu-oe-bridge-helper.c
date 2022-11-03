/*
 * Copyright 2022 Garmin Ltd. or its subsidiaries
 *
 * SPDX-License-Identifier: GPL-2.0
 *
 * Attempts to find and exec the host qemu-bridge-helper program
 */

#include <stdio.h>
#include <unistd.h>

void try_program(char const* path, char** args) {
    if (access(path, X_OK) == 0) {
        execv(path, args);
    }
}

int main(int argc, char** argv) {
    char* var;

    /* Copy arguments so that they are a NULL terminated list, skipping argv[0]
     * since it is this program name */
    char** args = malloc(argc * sizeof(char*));
    for (int i = 0; i < argc - 1; i++) {
        args[i] = argv[i + 1];
    }
    args[argc - 1] = NULL;

    var = getenv("QEMU_BRIDGE_HELPER");
    if (var && var[0] != '\0') {
        execvp(var, args);
        return 1;
    }

    try_program("/usr/libexec/qemu-bridge-helper", args);
    try_program("/usr/lib/qemu/qemu-bridge-helper", args);

    fprintf(stderr, "No bridge helper found\n");
    return 1;
}


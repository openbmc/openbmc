/*
 * Copyright 2021 Richard Purdie
 *
 * SPDX-License-Identifier: GPL-2.0-only
 */

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/*
 * Run the original script (argv[0] + ".real") with LD_LIBRARY_PATH unset
 * This avoids issues where cargo is running a wrapper script using /bin/sh from the host
 * which links to something which has an incompatible version in in recipe-sysroot-native
 * such as libtinfo on centos 7.
 */

int main(int argc, char* argv[]) {
    char *real = malloc(strlen(argv[0] + 5));
    strcpy(real, argv[0]);
    strcpy(real + strlen(argv[0]), ".real");
    putenv("LD_LIBRARY_PATH=");
    if(execv(real, argv) == -1) {
        printf("Wrapper failed to execute, error: %s\n", strerror(errno));
        return -1;
    }
}

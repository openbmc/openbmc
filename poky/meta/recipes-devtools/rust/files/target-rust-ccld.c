/*
*
* Copyright (C) 2022      Wind River Systems
*
* SPDX-License-Identifier: MIT
*
*/

#include <string.h>
#include <stdlib.h>
#include <unistd.h>

int main (int argc, char *argv[])
{
    unsetenv("LD_LIBRARY_PATH");
    execvp("target-rust-ccld-wrapper", argv);

    return 0;
}

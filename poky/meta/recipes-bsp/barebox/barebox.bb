SUMMARY = "barebox is a bootloader designed for embedded systems. It runs on a variety of architectures including x86, ARM, MIPS, PowerPC and others."
DESCRIPTION = "barebox aims to be a versatile and flexible bootloader not only for booting embedded Linux systems, \
but also for initial hardware bringup and development. \
Users should feel right at home with a shell with UNIX-like virtual file system access to hardware, \
Linux kernel driver API for making driver porting easier, \
and a subset of the POSIX C library for writing more command-line utilities."

require barebox-common.inc
inherit barebox


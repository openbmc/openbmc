#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Debugging tools"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PR = "r3"

MTRACE = ""
MTRACE_libc-glibc = "libc-mtrace"

STRACE = "strace"
STRACE_riscv32 = ""

RDEPENDS_${PN} = "\
    gdb \
    gdbserver \
    ${MTRACE} \
    ${STRACE} \
    "

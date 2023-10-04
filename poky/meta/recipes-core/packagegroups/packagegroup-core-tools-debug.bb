#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Debugging tools"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup


MTRACE = ""
MTRACE:libc-glibc = "libc-mtrace"

STRACE = "strace"
STRACE:riscv32 = ""

RDEPENDS:${PN} = "\
    gdb \
    gdbserver \
    ${MTRACE} \
    ${STRACE} \
    "

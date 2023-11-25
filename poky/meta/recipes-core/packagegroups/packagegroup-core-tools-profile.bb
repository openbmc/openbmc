#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Profiling tools"


PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# sysprof doesn't support aarch64 and nios2
PROFILE_TOOLS_SYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-analyze', '', d)}"

RRECOMMENDS:${PN} = "\
    ${PERF} \
    blktrace \
    ${PROFILE_TOOLS_SYSTEMD} \
    "

PROFILETOOLS = "\
    powertop \
    "
PERF = "perf"
PERF:libc-musl = ""
PERF:libc-musl:arm = "perf"
PERF:riscv32 = ""

# systemtap needs elfutils which is not fully buildable on some arches/libcs
SYSTEMTAP = "systemtap"
SYSTEMTAP:libc-musl = ""
SYSTEMTAP:nios2 = ""
SYSTEMTAP:riscv32 = ""

LTTNGTOOLS = "lttng-tools"
LTTNGTOOLS:arc = ""
LTTNGTOOLS:riscv32 = ""

BABELTRACE = "babeltrace"
BABELTRACE2 = "babeltrace2"

# valgrind does not work on the following configurations/architectures

VALGRIND = "valgrind"
VALGRIND:libc-musl = ""
VALGRIND:mipsarch = ""
VALGRIND:nios2 = ""
VALGRIND:arc = ""
VALGRIND:armv4 = ""
VALGRIND:armv5 = ""
VALGRIND:armv6 = ""
VALGRIND:armeb = ""
VALGRIND:aarch64 = ""
VALGRIND:riscv64 = ""
VALGRIND:riscv32 = ""
VALGRIND:powerpc = "${@bb.utils.contains('TARGET_FPU', 'soft', '', 'valgrind', d)}"
VALGRIND:linux-gnux32 = ""
VALGRIND:linux-gnun32 = ""

RDEPENDS:${PN} = "\
    ${PROFILETOOLS} \
    ${LTTNGTOOLS} \
    ${BABELTRACE} \
    ${BABELTRACE2} \
    ${SYSTEMTAP} \
    ${VALGRIND} \
    "

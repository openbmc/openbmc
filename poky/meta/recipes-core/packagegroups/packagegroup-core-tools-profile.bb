#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Profiling tools"

PR = "r3"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROFILE_TOOLS_X = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'sysprof', '', d)}"
# sysprof doesn't support aarch64 and nios2
PROFILE_TOOLS_X_aarch64 = ""
PROFILE_TOOLS_X_nios2 = ""
PROFILE_TOOLS_SYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-analyze', '', d)}"

RRECOMMENDS_${PN} = "\
    ${PERF} \
    blktrace \
    ${PROFILE_TOOLS_X} \
    ${PROFILE_TOOLS_SYSTEMD} \
    "

PROFILETOOLS = "\
    powertop \
    "
PERF = "perf"
PERF_libc-musl = ""

# systemtap needs elfutils which is not fully buildable on some arches/libcs
SYSTEMTAP = "systemtap"
SYSTEMTAP_libc-musl = ""
SYSTEMTAP_nios2 = ""
SYSTEMTAP_riscv64 = ""

# lttng-ust uses sched_getcpu() which is not there on for some platforms.
LTTNGUST = "lttng-ust"
LTTNGUST_libc-musl = ""
LTTNGUST_riscv64 = ""

LTTNGTOOLS = "lttng-tools"
LTTNGTOOLS_libc-musl = ""
LTTNGTOOLS_riscv64 = ""

LTTNGMODULES = "lttng-modules"
LTTNGMODULES_riscv64 = ""

BABELTRACE = "babeltrace"

# valgrind does not work on the following configurations/architectures

VALGRIND = "valgrind"
VALGRIND_libc-musl = ""
VALGRIND_mipsarch = ""
VALGRIND_nios2 = ""
VALGRIND_arc = ""
VALGRIND_armv4 = ""
VALGRIND_armv5 = ""
VALGRIND_armv6 = ""
VALGRIND_armeb = ""
VALGRIND_aarch64 = ""
VALGRIND_riscv64 = ""
VALGRIND_linux-gnux32 = ""
VALGRIND_linux-gnun32 = ""

RDEPENDS_${PN} = "\
    ${PROFILETOOLS} \
    ${LTTNGUST} \
    ${LTTNGTOOLS} \
    ${LTTNGMODULES} \
    ${BABELTRACE} \
    ${SYSTEMTAP} \
    ${VALGRIND} \
    "

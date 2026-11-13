SUMMARY = "A suite of basic system administration utilities"
HOMEPAGE = "https://en.wikipedia.org/wiki/Util-linux"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

SECTION = "base"

FILESEXTRAPATHS:prepend := "${THISDIR}/util-linux:"
MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
SRC_URI = "${KERNELORG_MIRROR}/linux/utils/util-linux/v${MAJOR_VERSION}/util-linux-${PV}.tar.xz \
           file://configure-sbindir.patch \
           file://runuser.pamd \
           file://runuser-l.pamd \
           file://ptest.patch \
           file://run-ptest \
           file://display_testname_for_subtest.patch \
           file://avoid_parallel_tests.patch \
           file://0001-tests-script-Disable-size-option-test.patch \
           file://0006-tests-lsfd-accept-rw-mode-for-mmap-ed-packet-socket-.patch \
           file://0001-tests-flock-increase-wait-for-flock-child-timeout.patch \
           "

SRC_URI[sha256sum] = "fbd62a100ab7bb8746ba0661255c3c48185b1e9021507c624da01fbc696330ec"

CVE_PRODUCT = "util-linux"

CVE_STATUS[CVE-2026-3184] = "fixed-version: Fixed since 2.42"

# util-linux publishes bugfix/security-only point releases on its
# stable/v<major.minor> branches.
inherit upstream-stable-release-point

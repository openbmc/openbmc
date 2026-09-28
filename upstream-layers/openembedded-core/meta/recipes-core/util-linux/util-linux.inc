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
           file://0001-tests-ts-kill-decode-avoid-using-shell-built-in-kill.patch \
           file://0001-lsfd-mkfds-foreign-sockets-skip-when-lacking-sock_di.patch \
           file://0001-ts-kill-decode-use-RTMIN-from-kill-L-instead-of-hard.patch \
           file://0001-tests-script-Disable-size-option-test.patch \
           file://CVE-2026-3184.patch \
           "

SRC_URI[sha256sum] = "f586e35d320ff537aab3ffeca37e9ecd482ccbe013590db4429a414d8aa6a728"

CVE_PRODUCT = "util-linux"

CVE_STATUS[CVE-2026-13595] = "cpe-stable-backport: Fixed from version >=2.41.5"

# util-linux publishes bugfix/security-only point releases on its
# stable/v<major.minor> branches.
inherit upstream-stable-release-point

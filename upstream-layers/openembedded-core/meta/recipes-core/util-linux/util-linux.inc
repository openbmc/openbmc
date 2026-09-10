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
           file://0001-libmount-add-missing-fileutils.h-include-to-hook_idma.patch \
           file://0002-lib-fileutils-fix-unused-parameter-warnings-without-.patch \
           file://0003-mount-fix-grammar-and-typo-in-X-mount.idmap-document.patch \
           file://0004-libmount-use-USE_LIBMOUNT_MOUNTFD_SUPPORT-for-idmap-h.patch \
           file://0005-tools-add-non-newmount.conf-config-gen-profile.patch \
           "

SRC_URI[sha256sum] = "66ac7c0e725278eb2b039e3104f2c91119341d941b41bac7a285c695f940bd57"

CVE_PRODUCT = "util-linux"

CVE_STATUS[CVE-2026-3184] = "fixed-version: Fixed since 2.42"

# util-linux publishes bugfix/security-only point releases on its
# stable/v<major.minor> branches.
inherit upstream-stable-release-point

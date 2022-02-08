DESCRIPTION = "The glog library implements application-level logging. This \
library provides logging APIs based on C++-style streams and various helper \
macros."
HOMEPAGE = "https://github.com/google/glog"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc9db360e0bbd4e46672f3fd91dd6c4b"

SRC_URI = " \
    git://github.com/google/glog.git;nobranch=1;protocol=https \
    file://0001-Rework-CMake-glog-VERSION-management.patch \
    file://0002-Find-Libunwind-during-configure.patch \
    file://0003-installation-path-fix.patch \
"

SRCREV = "a6a166db069520dbbd653c97c2e5b12e08a8bb26"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ?= "shared unwind"
PACKAGECONFIG_remove_riscv64 = "unwind"
PACKAGECONFIG_remove_riscv32 = "unwind"

PACKAGECONFIG[unwind] = "-DWITH_UNWIND=ON,-DWITH_UNWIND=OFF,libunwind,libunwind"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"

do_configure_append() {
    # remove WORKDIR info to improve reproducibility
    if [ -f  "${B}/config.h" ] ; then
        sed -i 's/'$(echo ${WORKDIR} | sed 's_/_\\/_g')'/../g' ${B}/config.h
    fi
}

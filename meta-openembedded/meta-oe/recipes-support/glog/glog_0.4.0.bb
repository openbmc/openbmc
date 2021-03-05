DESCRIPTION = "The glog library implements application-level logging. This \
library provides logging APIs based on C++-style streams and various helper \
macros."
HOMEPAGE = "https://github.com/google/glog"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc9db360e0bbd4e46672f3fd91dd6c4b"

SRC_URI = " \
    git://github.com/google/glog.git;nobranch=1 \
    file://0001-Find-Libunwind-during-configure.patch \
"

SRCREV = "96a2f23dca4cc7180821ca5f32e526314395d26a"

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

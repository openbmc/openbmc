DESCRIPTION = "The glog library implements application-level logging. This \
library provides logging APIs based on C++-style streams and various helper \
macros."
HOMEPAGE = "https://github.com/google/glog"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc9db360e0bbd4e46672f3fd91dd6c4b"

DEPENDS = "libunwind"

SRC_URI = " \
    git://github.com/google/glog.git;branch=v035 \
    file://0001-Rework-CMake-glog-VERSION-management.patch \
    file://0002-Find-Libunwind-during-configure.patch \
    file://0003-installation-path-fix.patch \
"

SRCREV = "a6a166db069520dbbd653c97c2e5b12e08a8bb26"

S = "${WORKDIR}/git"

inherit cmake

RDEPENDS_${PN} += "libunwind"

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

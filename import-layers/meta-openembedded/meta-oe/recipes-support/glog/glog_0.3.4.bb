DESCRIPTION = "The glog library implements application-level logging. This \
library provides logging APIs based on C++-style streams and various helper \
macros."
HOMEPAGE = "https://github.com/google/glog"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=dc9db360e0bbd4e46672f3fd91dd6c4b"

DEPENDS = "libunwind"

SRC_URI = " \
    git://github.com/google/glog.git \
    file://0001-configure.ac-Allow-user-to-disable-gflags.patch \
"

SRCREV = "d8cb47f77d1c31779f3ff890e1a5748483778d6a"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gflags] = ",--without-gflags,gflags,"

inherit autotools pkgconfig

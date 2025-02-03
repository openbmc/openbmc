SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSING;md5=c0a30e2b1502c55a7f37e412cd6c6a4b \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    "

inherit autotools pkgconfig

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH};protocol=https \
           file://fix_cflags_handling.patch \
           "
SRCREV = "e5714d1f0a477f91ce3986fa63651c7710f0e183"
SRCBRANCH ?= "master"

PROVIDES = "virtual/crypt"

S = "${WORKDIR}/git"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE}"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir} -Wno-error"
CPPFLAGS:append:class-nativesdk = " -Wno-error"

API = "--disable-obsolete-api"
EXTRA_OECONF += "${API}"

BBCLASSEXTEND = "native nativesdk"

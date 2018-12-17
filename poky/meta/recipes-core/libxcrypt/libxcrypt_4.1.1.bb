SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM ?= "file://LICENSING;md5=cb3ca4cabd2447a37bf186fad6f79852 \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit autotools pkgconfig

SRCREV ?= "21b455b68baad279e6a3936faced16c5e5634376"
SRCBRANCH ?= "develop"

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH} \
           "

PROVIDES = "virtual/crypt"

FILES_${PN} = "${libdir}/libcrypt*.so.* ${libdir}/libcrypt-*.so ${libdir}/libowcrypt*.so.* ${libdir}/libowcrypt-*.so"

S = "${WORKDIR}/git"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE} -std=gnu99"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir}"

BBCLASSEXTEND = "nativesdk"

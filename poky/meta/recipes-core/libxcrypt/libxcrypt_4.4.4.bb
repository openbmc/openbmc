SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM ?= "file://LICENSING;md5=be275bc7f91642efe7709a8ae7a1433b \
                     file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit autotools pkgconfig

PV = "4.4.4"

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH}"
SRCREV = "3b251d4e1af66aa7697c4ac96c689f996fa90c32"
SRCBRANCH ?= "develop"

PROVIDES = "virtual/crypt"

FILES_${PN} = "${libdir}/libcrypt*.so.* \
               ${libdir}/libcrypt-*.so \
               ${libdir}/libowcrypt*.so.* \
               ${libdir}/libowcrypt-*.so \
"

S = "${WORKDIR}/git"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE}"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir} -Wno-error=missing-attributes"
CPPFLAGS_append_class-nativesdk = " -Wno-error=missing-attributes"

BBCLASSEXTEND = "nativesdk"

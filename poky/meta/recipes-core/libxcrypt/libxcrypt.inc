SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSING;md5=bd5d9777dfe7076c4f2928f12fed226a \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    "

inherit autotools pkgconfig

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH}"
SRCREV = "94d84f92ca123d851586016c4678eb1f21c19029"
SRCBRANCH ?= "develop"

PROVIDES = "virtual/crypt"

FILES_${PN} = "${libdir}/libcrypt*.so.* \
               ${libdir}/libcrypt-*.so \
               ${libdir}/libowcrypt*.so.* \
               ${libdir}/libowcrypt-*.so \
"

S = "${WORKDIR}/git"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE}"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir} -Wno-error"
CPPFLAGS_append_class-nativesdk = " -Wno-error"

API = "--disable-obsolete-api"
EXTRA_OECONF += "${API}"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM ?= "file://LICENSING;md5=be275bc7f91642efe7709a8ae7a1433b \
                     file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit autotools pkgconfig

PV = "4.4.6"

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH}"
SRCREV = "398943774c5ff38baf1bc5ee088855fd8983bb05"
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

API = "--disable-obsolete-api"
EXTRA_OECONF += "${API}"

BBCLASSEXTEND = "nativesdk"

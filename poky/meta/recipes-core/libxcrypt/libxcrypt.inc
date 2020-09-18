SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSING;md5=102923b6e1b02a85c5a1203fa87d151d \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    "

inherit autotools pkgconfig

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH}"
SRCREV = "6b110bcd4f4caa61fc39c7339d30adc20a7dd177"
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

SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPL-2.1-only & 0BSD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSING;md5=b65434749329c34b5d32c2df3f1b4112 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    "

inherit autotools pkgconfig

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "db70b42bd7b2a5b00a8580c8dec0aa66791c950a"

PROVIDES = "virtual/crypt"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE}"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir} -Wno-error"
CPPFLAGS:append:class-nativesdk = " -Wno-error"

API = "--disable-obsolete-api"
EXTRA_OECONF += "${API}"

BBCLASSEXTEND = "native nativesdk"
# Needed until https://github.com/besser82/libxcrypt/issues/181 is addressed
LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', '-Wl,--undefined-version', '', d)}"

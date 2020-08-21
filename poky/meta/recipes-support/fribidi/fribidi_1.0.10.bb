SUMMARY = "Free Implementation of the Unicode Bidirectional Algorithm"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.xz \
           "
SRC_URI[md5sum] = "97c87da9930e8e70fbfc8e2bcd031554"
SRC_URI[sha256sum] = "7f1c687c7831499bcacae5e8675945a39bacbad16ecaa945e9454a32df653c01"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit meson lib_package pkgconfig

CVE_PRODUCT = "gnu_fribidi fribidi"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "Free Implementation of the Unicode Bidirectional Algorithm"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
           file://meson.patch"
SRC_URI[md5sum] = "0f97f65038545340316ec0f4bd53df0b"
SRC_URI[sha256sum] = "6a64f2a687f5c4f203a46fa659f43dd43d1f8b845df8d723107e8a7e6158e4ce"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit meson lib_package pkgconfig

CVE_PRODUCT = "gnu_fribidi"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "a0bb49765ceccc293ab2c6735ba100431807d384ffa14c2ebd30e07993fd2fa4"

EXTRA_OEMESON = "-Dbuild-documentation=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm"


inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "a0bb49765ceccc293ab2c6735ba100431807d384ffa14c2ebd30e07993fd2fa4"

EXTRA_OEMESON = "-Dbuild-documentation=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

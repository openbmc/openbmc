SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm"

inherit distro_features_check gnome autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "d53b60b0f1be597e86070954a49cf0c3"
SRC_URI[archive.sha256sum] = "ff95385759e2af23828d4056356f25376cfabc41e690ac1df055371537e458bd"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

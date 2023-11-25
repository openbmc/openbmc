SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm-2.68"

GNOMEBN = "atkmm"

inherit gnomebase features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "6f62dd99f746985e573605937577ccfc944368f606a71ca46342d70e1cdae079"

S = "${WORKDIR}/${GNOMEBN}-${PV}"

EXTRA_OEMESON = "-Dbuild-documentation=false"

FILES:${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm"

inherit features_check gnomebase autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "6194ac577f15567adfa3c923944c6651"
SRC_URI[archive.sha256sum] = "4c4cfc917fd42d3879ce997b463428d6982affa0fb660cafcc0bc2d9afcedd3a"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

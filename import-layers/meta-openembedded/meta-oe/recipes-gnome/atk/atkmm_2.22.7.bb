SUMMARY = "C++ bindings for the atk"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atk glibmm"

inherit gnome autotools pkgconfig

GNOME_COMPRESS_TYPE = "xz"

SRC_URI[archive.md5sum] = "fec7db3fc47ba2e0c95d130ec865a236"
SRC_URI[archive.sha256sum] = "bfbf846b409b4c5eb3a52fa32a13d86936021969406b3dcafd4dd05abd70f91b"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

SUMMARY = "C++ bindings for the pango library"
SECTION = "libs"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "mm-common cairomm glibmm pango"

SHRT_VER = "${@d.getVar('PV',1).split('.')[0]}.${@d.getVar('PV',1).split('.')[1]}"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/pangomm/${SHRT_VER}/pangomm-${PV}.tar.xz"
SRC_URI[md5sum] = "62910723211d86ab825b666b479871c9"
SRC_URI[sha256sum] = "a8d96952c708d7726bed260d693cece554f8f00e48b97cccfbf4f5690b6821f0"

inherit autotools pkgconfig

EXTRA_OECONF = " --disable-documentation "

FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${libdir}/*/include/ ${libdir}/pangomm-*/"


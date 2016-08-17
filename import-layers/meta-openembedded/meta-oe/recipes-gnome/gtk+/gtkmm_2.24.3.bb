SUMMARY = "C++ bindings for the GTK+ toolkit"
HOMEPAGE = "http://www.gtkmm.org/"
SECTION = "libs"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
                    file://COPYING.tools;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "atkmm pangomm glibmm gtk+ cairomm"

inherit gnome autotools pkgconfig

GNOME_COMPRESS_TYPE = "xz"

SRC_URI[archive.md5sum] = "42fc5a3feeb33ea59b7660200e2a5465"
SRC_URI[archive.sha256sum] = "c564a438677174b97d69dd70467cb03c933481006398dc9377417aa6abe02a39"

EXTRA_OECONF = " --disable-documentation "

FILES_${PN}-dev += "${libdir}/*/include ${libdir}/*/proc/m4"

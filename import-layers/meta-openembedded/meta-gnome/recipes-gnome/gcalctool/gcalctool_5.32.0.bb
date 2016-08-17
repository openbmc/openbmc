SUMMARY = "gcalctool is a powerful calculator"
SECTION = "x11"
DEPENDS = "gtk+ gnome-doc-utils"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SHRT_VER = "${@d.getVar('PV',1).split('.')[0]}.${@d.getVar('PV',1).split('.')[1]}"
SRC_URI = "http://download.gnome.org/sources/${BPN}/${SHRT_VER}/${BP}.tar.gz"

SRC_URI[md5sum] = "48db927c6e7ee1d5395f953a8c184c98"
SRC_URI[sha256sum] = "346f645c0fdef900642f6e9a2c18e7ba9a7ca9bc62fe2b08eb418f065a425c89"

inherit autotools pkgconfig gsettings

do_configure_prepend() {
    sed -i -e "s: help: :g" ${S}/Makefile.am
}

PNBLACKLIST[gcalctool] ?= "broken at least since April 2013 http://permalink.gmane.org/gmane.comp.handhelds.openembedded/57920 and nobody complained, it's also replaced by gnome calculator"

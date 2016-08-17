LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "x11/gnome/libs"
DEPENDS = "libxml2 gconf libsoup-2.4 gtk+3"

PNBLACKLIST[libgweather3] ?= "CONFLICT: 876 files are conflicting with libgweather"
# e.g. sysroots/qemux86-64/usr/share/libgweather/locations.dtd
#      sysroots/qemux86-64/usr/share/libgweather/Locations.zh_TW.xml

BPN = "libgweather"

inherit gnome
SRC_URI[archive.md5sum] = "f1a96c6f19c9a0bc6b4e12acc9a8a85d"
SRC_URI[archive.sha256sum] = "9041526fa0466b99dae5cf06c2cc70376f25531eec5d58b1e1378acfb302410c"

do_configure_prepend() {
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}

FILES_${PN} += "${datadir}/gnome* \
                ${datadir}/icons"

PACKAGES =+ "${PN}-locationdata"
FILES_${PN}-locationdata = "${datadir}/libgweather/*ocations*"




LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome/libs"
DEPENDS = "libxml2 gconf libsoup-2.4 gtk+ libgnome-keyring"

inherit gnome

SRC_URI[archive.md5sum] = "bf6a0a05051341ecb250f332e3edfb88"
SRC_URI[archive.sha256sum] = "b835374661423f37c46aa8e37368ae24a68856f117b7c21e475a21efdba5264c"
GNOME_COMPRESS_TYPE="bz2"

do_configure_prepend() {
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}

FILES_${PN} += "${datadir}/gnome* \
                ${datadir}/icons"

PACKAGES =+ "${PN}-locationdata"
FILES_${PN}-locationdata = "${datadir}/libgweather/Locations*"



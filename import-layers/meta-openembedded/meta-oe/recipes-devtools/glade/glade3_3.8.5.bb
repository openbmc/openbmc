SUMMARY = "Glade - A User Interface Designer"
HOMEPAGE = "http://www.gnu.org/software/gnash"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=aabe87591cb8ae0f3c68be6977bb5522 \
                    file://COPYING.GPL;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYING.LGPL;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "gtk+ gnome-doc-utils gnome-common libxml2 intltool-native"

inherit autotools pkgconfig pythonnative gtk-icon-cache

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/glade3/3.8/glade3-${PV}.tar.xz \
           file://0001-gnome-doc-utils.make-sysrooted-pkg-config.patch \
           file://0002-fix-gcc-6-build.patch \
           file://0001-Add-G_GNUC_PRINTF-on-functions-with-format-strings.patch \
           "
SRC_URI[md5sum] = "4e4b4f5ee34a03e017e4cef97d796c1f"
SRC_URI[sha256sum] = "58a5f6e4df4028230ddecc74c564808b7ec4471b1925058e29304f778b6b2735"

EXTRA_OECONF += "--disable-scrollkeeper"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gnome] = "--enable-gnome,--disable-gnome,libbonoboui libgnomeui"

FILES_${PN} += "${datadir}/icons"

SUMMARY = "Lightweight, desktop independent GTK+ archive manager"
HOMEPAGE = "http://xarchiver.sourceforge.net"
SECTION = "x11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+ glib-2.0 xfce4-dev-tools-native intltool-native"

SRC_URI = "git://github.com/schnitzeltony/xarchiver.git;branch=master"
SRCREV = "e80e90528c9aab2fe36d9078b945b44c05cc20d3"
PV = "0.5.3"
S = "${WORKDIR}/git"

inherit xfce-git gettext pkgconfig autotools gtk-icon-cache

# install tap files for thunar-archive-plugin in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"

EXTRA_OECONF += "--enable-maintainer-mode"

FILES_${PN} += "${libdir}/thunar-archive-plugin"

RRECOMMENDS_${PN} = "lzop zip tar bzip2 unzip xz"

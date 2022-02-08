SUMMARY = "Lightweight, desktop independent GTK+ archive manager"
HOMEPAGE = "http://xarchiver.sourceforge.net"
SECTION = "x11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "gtk+3 glib-2.0 xfce4-dev-tools-native intltool-native"

SRC_URI = "git://github.com/ib/xarchiver.git;branch=master;protocol=https"
SRCREV = "9ab958a4023b62b43972c55a3143ff0722bd88a6"
PV = "0.5.4.14"
S = "${WORKDIR}/git"

inherit gettext pkgconfig autotools gtk-icon-cache features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

# install tap files for thunar-archive-plugin in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"

EXTRA_OECONF += " \
    --enable-maintainer-mode \
    --disable-doc \
"

do_configure_prepend() {
     touch ${S}/NEWS ${S}/AUTHORS
}

FILES_${PN} += "${libdir}/thunar-archive-plugin"

RRECOMMENDS_${PN} = "lzop zip tar bzip2 unzip xz p7zip"

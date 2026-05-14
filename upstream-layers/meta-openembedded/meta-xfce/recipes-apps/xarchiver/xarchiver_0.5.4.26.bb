SUMMARY = "Lightweight, desktop independent GTK+ archive manager"
HOMEPAGE = "http://xarchiver.sourceforge.net"
SECTION = "x11"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 glib-2.0 xfce4-dev-tools-native intltool-native"

SRC_URI = "git://github.com/ib/xarchiver.git;branch=master;protocol=https;tag=${PV}"
SRCREV = "e14d54e1c85f4dbc78b89042017ebdc0fe5e2f4c"


inherit gettext pkgconfig autotools gtk-icon-cache features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

# install tap files for thunar-archive-plugin in ${libdir}/thunar-archive-plugin
EXTRA_OECONF += "--libexecdir=${libdir}"

EXTRA_OECONF += " \
    --enable-maintainer-mode \
    --disable-doc \
"

do_configure:prepend() {
     touch ${S}/NEWS ${S}/AUTHORS
}

FILES:${PN} += "${libdir}/thunar-archive-plugin"

RRECOMMENDS:${PN} = "lzop zip tar bzip2 unzip xz 7zip"

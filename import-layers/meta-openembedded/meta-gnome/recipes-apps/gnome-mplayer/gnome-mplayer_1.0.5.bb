SUMMARY = "A GTK+ interface to MPlayer"
HOMEPAGE = "http://code.google.com/p/gnome-mplayer"
SECTION = "multimedia"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PNBLACKLIST[gnome-mplayer] ?= "rdepends on blacklisted mplayer"
PR = "r2"

DEPENDS = "gmtk gtk+ alsa-lib libnotify glib-2.0 dbus-glib virtual/libx11 libxscrnsaver"

SRC_URI = "http://${BPN}.googlecode.com/files/${BP}.tar.gz"
SRC_URI[md5sum] = "1d3ab24c5501b5528e357931ca4dc6da"
SRC_URI[sha256sum] = "ac3c179345baecb4ca5237782aa33e83253a87bf8b42ce6eb3a9207a340f61b2"

EXTRA_OECONF = "--with-gio --with-alsa --with-dbus --with-libnotify"

FILES_${PN} += "${datadir}/gnome-control-center/default-apps/${PN}.xml"
PACKAGES =+ "${PN}-nautilus-extension"
FILES_${PN}-nautilus-extension += "${libdir}/nautilus/extensions-2.0/*.so \
                                   ${libdir}/nautilus/extensions-3.0/*.so"
FILES_${PN}-dev += "${libdir}/nautilus/extensions-2.0/*.la \
                    ${libdir}/nautilus/extensions-3.0/*.la"
FILES_${PN}-staticdev += "${libdir}/nautilus/extensions-2.0/*.a \
                          ${libdir}/nautilus/extensions-3.0/*.a"
FILES_${PN}-dbg += "${libdir}/nautilus/extensions-2.0/.debug \
                    ${libdir}/nautilus/extensions-3.0/.debug"

inherit gettext pkgconfig mime gtk-icon-cache autotools

RDEPENDS_${PN} = "mplayer"

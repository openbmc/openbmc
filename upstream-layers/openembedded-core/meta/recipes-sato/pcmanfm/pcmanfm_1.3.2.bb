SUMMARY = "Fast lightweight tabbed filemanager"
DESCRIPTION = "A free file manager application and the standard file manager of LXDE."
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://src/pcmanfm.h;endline=22;md5=417b3855771a3a87f8ad753d994491f0 \
                    file://src/gseal-gtk-compat.h;endline=21;md5=46922c8691f58d124f9420fe16149ce2"

SECTION = "x11"
DEPENDS = "gtk+3 startup-notification libfm intltool-native gettext-native glib-2.0-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/pcmanfm-${PV}.tar.xz \
	   file://gnome-fs-directory.png \
	   file://gnome-fs-regular.png \
	   file://gnome-mime-text-plain.png \
	   file://emblem-symbolic-link.png \
	   file://no-desktop.patch"

SRC_URI[sha256sum] = "14cb7b247493c4cce65fbb5902611e3ad00a7a870fbc1e50adc50428c5140cf7"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/pcmanfm/files/PCManFM%20%2B%20Libfm%20%28tarball%20release%29/PCManFM/"

inherit autotools pkgconfig features_check mime-xdg

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "--with-gtk=3"
# GCC 14 finds extra incompatible pointer type warnings which are treated as errors
CFLAGS += "-Wno-error=incompatible-pointer-types"

do_install:append () {
	install -d ${D}/${datadir}
	install -d ${D}/${datadir}/pixmaps/

	install -m 0644 ${UNPACKDIR}/*.png ${D}/${datadir}/pixmaps
}

FILES:${PN} += "${libdir}/pcmanfm"

RRECOMMENDS:${PN} += "adwaita-icon-theme"

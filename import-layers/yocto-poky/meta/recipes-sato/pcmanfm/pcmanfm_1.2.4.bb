SUMMARY = "Fast lightweight tabbed filemanager"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "GPLv2 & GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://src/pcmanfm.h;endline=22;md5=417b3855771a3a87f8ad753d994491f0 \
                    file://src/gseal-gtk-compat.h;endline=21;md5=46922c8691f58d124f9420fe16149ce2"

SECTION = "x11"
DEPENDS = "gtk+ startup-notification libfm intltool-native gettext-native"
DEPENDS_append_poky = " libowl"


COMPATIBLE_HOST = '(x86_64.*|i.86.*|aarch64.*|arm.*|mips.*|powerpc.*|sh.*)-(linux|freebsd.*)'

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/pcmanfm-${PV}.tar.xz \
	   file://gnome-fs-directory.png \
	   file://gnome-fs-regular.png \
	   file://gnome-mime-text-plain.png \
	   file://emblem-symbolic-link.png \
	   file://no-desktop.patch"

SRC_URI[md5sum] = "19764c2f59653724c8713e0064fa6829"
SRC_URI[sha256sum] = "38cdbb5f01d24483b41b8e6846e4aa66a5751bb3982a8618899e88a853dbe313"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/pcmanfm/files/PCManFM%20%2B%20Libfm%20%28tarball%20release%29/PCManFM/"

inherit autotools pkgconfig distro_features_check

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

do_install_append () {
	install -d ${D}/${datadir}
	install -d ${D}/${datadir}/pixmaps/

	install -m 0644 ${WORKDIR}/*.png ${D}/${datadir}/pixmaps
}

FILES_${PN} += "${libdir}/pcmanfm"

RRECOMMENDS_${PN} += "adwaita-icon-theme"

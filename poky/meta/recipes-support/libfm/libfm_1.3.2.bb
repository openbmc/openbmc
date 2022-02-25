SUMMARY = "Library for file management"
DESCRIPTION = "LibFM provides file management functions built on top of Glib/GIO \
giving a convenient higher-level API."
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b \
                    file://src/fm.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/base/fm-config.h;beginline=10;endline=23;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/fm-gtk.h;beginline=6;endline=19;md5=646baa4955c04fe768f2ca27b92ac8dd"


SECTION = "x11/libs"
DEPENDS = "glib-2.0 glib-2.0-native pango gtk+3 menu-cache intltool-native libexif libfm-extra"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-Correctly-check-the-stamp-file-that-indicates-if-we-.patch \
           file://0001-Do-not-add-library-path-to-avoid-host-contamination.patch \
           "

SRC_URI[sha256sum] = "a5042630304cf8e5d8cff9d565c6bd546f228b48c960153ed366a34e87cad1e5"

inherit autotools pkgconfig gtk-doc gettext features_check mime mime-xdg
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

EXTRA_OECONF = "--with-gtk=3"

do_configure[dirs] =+ "${S}/m4"

PACKAGES =+ "libfm-gtk"
PACKAGES += "${PN}-mime"
FILES:libfm-gtk = " \
    ${libdir}/libfm-gtk*so.* \
    ${libdir}/libfm/modules/gtk* \
    ${bindir}/libfm-pref-apps \
    ${bindir}/lxshortcut \
    ${datadir}/applications/libfm-pref-apps.desktop \
    ${datadir}/applications/lxshortcut.desktop \
    ${datadir}/libfm/images/folder.png \
    ${datadir}/libfm/images/unknown.png \
    ${datadir}/libfm/ui/*.ui \
"
FILES:${PN}-mime = "${datadir}/mime/"

do_install:append () {
    # remove files which are part of libfm-extra
    rm -f ${D}${includedir}/libfm-1.0/fm-xml-file.h
    rm -f ${D}${includedir}/libfm-1.0/fm-version.h
    rm -f ${D}${includedir}/libfm-1.0/fm-extra.h
    rm -f ${D}${includedir}/libfm
    rm -f ${D}${libdir}/pkgconfig/libfm-extra.pc
    rm -f ${D}${libdir}/libfm-extra.so*
    rm -f ${D}${libdir}/libfm-extra.a
    rm -f ${D}${libdir}/libfm-extra.la
}

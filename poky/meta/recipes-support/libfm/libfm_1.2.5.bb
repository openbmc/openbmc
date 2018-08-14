SUMMARY = "Library for file management"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b \
                    file://src/fm.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/base/fm-config.h;beginline=10;endline=23;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/fm-gtk.h;beginline=6;endline=19;md5=646baa4955c04fe768f2ca27b92ac8dd"


SECTION = "x11/libs"
DEPENDS = "glib-2.0 glib-2.0-native pango gtk+3 menu-cache intltool-native libexif libfm-extra"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz \
           file://0001-Correctly-check-the-stamp-file-that-indicates-if-we-.patch \
           file://0001-Enclose-text-in-programlisting-into-CDATA.patch \
           "

SRC_URI[md5sum] = "a1ba9ae5e920f38b647dd511edd6c807"
SRC_URI[sha256sum] = "c706bb1020cf5f2d6f5a9226f692ce1985947134dcf2bde64278bd0420779b5a"

inherit autotools pkgconfig gtk-doc gettext distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

EXTRA_OECONF = "--with-gtk=3"

do_configure[dirs] =+ "${S}/m4"

PACKAGES =+ "libfm-gtk"
PACKAGES += "${PN}-mime"
FILES_libfm-gtk = " \
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
FILES_${PN}-mime = "${datadir}/mime/"

do_install_append () {
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

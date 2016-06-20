SUMMARY = "Library for file management"
HOMEPAGE = "http://pcmanfm.sourceforge.net/"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b \
                    file://src/fm.h;beginline=8;endline=21;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/base/fm-config.h;beginline=10;endline=23;md5=ef1f84da64b3c01cca447212f7ef6007 \
                    file://src/fm-gtk.h;beginline=6;endline=19;md5=646baa4955c04fe768f2ca27b92ac8dd"


SECTION = "x11/libs"
DEPENDS = "glib-2.0 pango gtk+ menu-cache intltool-native libexif libfm-extra gettext-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/pcmanfm/libfm-${PV}.tar.xz"

SRC_URI[md5sum] = "74997d75e7e87dc73398746fd373bf52"
SRC_URI[sha256sum] = "7804f6f28cb3d1bc8ffb3151ab7ff0c063b27c5f9b06c682eb903e01cf25502f"

inherit autotools pkgconfig gtk-doc distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

do_configure[dirs] =+ "${S}/m4"

PACKAGES += "${PN}-mime"
FILES_${PN}-mime = "${datadir}/mime/"

do_install_append () {
    # remove files which are part of libfm-extra
    rm -f ${D}${includedir}/libfm-1.0/fm-xml-file.h
    rm -f ${D}${includedir}/libfm-1.0/fm-version.h
    rm -f ${D}${includedir}/libfm-1.0/fm-extra.h
    rm -f ${D}${libdir}/pkgconfig/libfm-extra.pc
    rm -f ${D}${libdir}/libfm-extra.so*
    rm -f ${D}${libdir}/libfm-extra.a
    rm -f ${D}${libdir}/libfm-extra.la
}

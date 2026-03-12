SUMMARY = "Openbox configuration tool"
HOMEPAGE = "http://openbox.org/wiki/ObConf:About"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = " \
    openbox \
    startup-notification \
    gtk+3 \
"
PV = "2.0.4+git"

SRC_URI = "http://deb.debian.org/debian/pool/main/o/obconf/obconf_2.0.4+git20150213.orig.tar.xz \
           file://0001-Fix-function-protype-visibility.patch"
SRC_URI[sha256sum] = "2c8837de833a4f2859ddf06e512d7d96d9a7623f90f95d1137779c69e9209feb"

S = "${UNPACKDIR}/${BPN}"

inherit autotools gettext pkgconfig mime mime-xdg features_check
# depends on openbox, which is X11-only
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_AUTORECONF = ""

FILES:${PN} += "\
    ${datadir}/mime \
"

do_install:append () {
    rm -rf ${D}${datadir}/mimelnk
}

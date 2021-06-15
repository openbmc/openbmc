SUMMARY = "Openbox configuration tool"
AUTHOR = "Dana Jansens & Tim Riley & Javeed Shaikh"
HOMEPAGE = "http://openbox.org/wiki/ObConf:About"
SECTION = "x11/wm"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = " \
    openbox \
    startup-notification \
    gtk+3 \
"
PV = "2.0.4+git${SRCPV}"

SRCREV = "63ec47c5e295ad4f09d1df6d92afb7e10c3fec39"
SRC_URI = " \
    git://git.openbox.org/dana/obconf \
"

S = "${WORKDIR}/git"

inherit autotools gettext pkgconfig mime mime-xdg features_check
# depends on openbox, which is X11-only
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_AUTORECONF = ""

FILES_${PN} += "\
    ${datadir}/mime \
"

do_install_append () {
    rm -rf ${D}${datadir}/mimelnk
}

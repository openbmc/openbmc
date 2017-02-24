SUMMARY = "Touchscreen calibration utility"

DESCRIPTION = "Basic touchscreen calibration utility"

HOMEPAGE = "http://gpe.linuxtogo.org"
BUGTRACKER = "http://bugs.linuxtogo.org"
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://main.c;endline=10;md5=8721bcb08ae9f24e5fe4c82941873c87"

SECTION = "x11/base"

DEPENDS = "virtual/libx11 libxft libxcalibrate"

PR = "r13"

SRC_URI = "${GPE_MIRROR}/xtscal-${PV}.tar.bz2 \
           file://change-cross.patch \
           file://cleanup.patch \
           file://dso_linking_change_build_fix.patch \
           file://30xTs_Calibrate.sh"

SRC_URI[md5sum] = "9bcab80b474d5454477d1ca166a68c34"
SRC_URI[sha256sum] = "27b9dc2203de9b1706ca39fa6ca80ecab8807909ec901c4a345b8e41178800a1"

inherit autotools pkgconfig distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

do_install_append() {
    install -d ${D}${sysconfdir}/X11/Xsession.d/
    install -m 0755 ${WORKDIR}/30xTs_Calibrate.sh ${D}${sysconfdir}/X11/Xsession.d/
}

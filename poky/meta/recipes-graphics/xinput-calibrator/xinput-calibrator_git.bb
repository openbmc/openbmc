SUMMARY = "Touchscreen calibration program for X11"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/xinput_calibrator"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://src/calibrator.cpp;endline=22;md5=1bcba08f67cdb56f34021557898e4b5a"
DEPENDS = "virtual/libx11 libxi libxrandr"

PV = "0.7.5+git${SRCPV}"
PR = "r6"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRCREV = "03dadf55109bd43d3380f040debe9f82f66f2f35"
SRC_URI = "git://github.com/tias/xinput_calibrator.git \
           file://30xinput_calibrate.sh \
           file://Allow-xinput_calibrator_pointercal.sh-to-be-run-as-n.patch \
           file://0001-calibrator.hh-Include-string-to-get-std-string.patch \
           "

S = "${WORKDIR}/git"

# force native X11 ui as we don't have gtk+ in DEPENDS
EXTRA_OECONF += "--with-gui=x11"

do_install_append() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/scripts/xinput_calibrator_pointercal.sh ${D}${bindir}/xinput_calibrator_once.sh

    install -d ${D}${sysconfdir}/X11/Xsession.d/
    install -m 0755 ${WORKDIR}/30xinput_calibrate.sh ${D}${sysconfdir}/X11/Xsession.d/

    install -d ${D}${sysconfdir}/xdg/autostart
    sed -e 's,^Exec=.*,Exec=${bindir}/xinput_calibrator_once.sh,' ${S}/scripts/xinput_calibrator.desktop > ${D}${sysconfdir}/xdg/autostart/xinput_calibrator.desktop
}

FILES_${PN} += "${sysconfdir}/xdg/autostart"
RDEPENDS_${PN} = "xinput formfactor"
RRECOMMENDS_${PN} = "pointercal-xinput"

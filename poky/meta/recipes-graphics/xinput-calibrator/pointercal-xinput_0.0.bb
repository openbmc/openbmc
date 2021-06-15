SUMMARY = "Touchscreen calibration data from xinput-calibrator"
DESCRIPTION = "A generic touchscreen calibration program for X.Org"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/xinput_calibrator/"
BUGTRACKER = "https://github.com/tias/xinput_calibrator/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r7"

SRC_URI = "file://pointercal.xinput"
S = "${WORKDIR}"

do_install() {
    # Only install file if it has a contents
    if [ -s ${S}/pointercal.xinput ] &&\
       [ ! -n "$(head -n1 ${S}/pointercal.xinput|grep "replace.*pointercal\.xinput")" ]; then
        install -d ${D}${sysconfdir}/
        install -m 0644 ${S}/pointercal.xinput ${D}${sysconfdir}/
    fi
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
CONFFILES_${PN} = "${sysconfdir}/pointercal.xinput"

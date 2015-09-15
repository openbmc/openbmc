SUMMARY = "Matchbox desktop folders for the Sato environment"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SECTION = "x11"
DEPENDS = ""
RCONFLICTS_${PN} = "matchbox-common"

SRCREV = "810b0b08eb79e4685202da2ec347b990bb467e07"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://git.yoctoproject.org/${BPN}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES_${PN} += "${datadir}"

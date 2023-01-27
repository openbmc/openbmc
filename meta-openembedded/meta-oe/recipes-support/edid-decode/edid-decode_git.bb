SUMMARY = "Decode EDID data in human-readable format"
DESCRIPTION = "edid-decode decodes EDID monitor description data in human-readable format."
AUTHOR = "Hans Verkuil <hverkuil-cisco@xs4all.nl>"
HOMEPAGE = "https://hverkuil.home.xs4all.nl/edid-decode/edid-decode.html"

SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ef696d66c156139232201f223c22592"

SRC_URI= "git://git.linuxtv.org/edid-decode.git;protocol=https;branch=master"
SRCREV = "e052f5f9fdf74ca11aa1a8edfa62eff8d0aa3d0d"
PV = "0.0+git${SRCPV}"
S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

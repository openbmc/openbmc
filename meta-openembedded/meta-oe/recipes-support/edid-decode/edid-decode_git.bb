SUMMARY = "Decode EDID data in human-readable format"
DESCRIPTION = "edid-decode decodes EDID monitor description data in human-readable format."
HOMEPAGE = "https://hverkuil.home.xs4all.nl/edid-decode/edid-decode.html"

SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ef696d66c156139232201f223c22592"

SRC_URI= "git://git.linuxtv.org/edid-decode.git;protocol=https;branch=master"
SRCREV = "5920bf2a756b2f748c49ff6a08b9f421026473c5"
PV = "0.0+git"
S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

BBCLASSEXTEND = "native"

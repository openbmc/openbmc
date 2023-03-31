SUMMARY = "copy-paste for the Linux console"
DESCRIPTION = "Consolation is a daemon that provides copy-paste and scrolling \
support to the Linux console. It is based on the libinput library and \
supports all pointer devices and settings provided by this library. Similar \
software include gpm and jamd."
HOMEPAGE = "https://salsa.debian.org/consolation-team/consolation"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7df9eea2f4dfdda489c116099e6fc062"

DEPENDS = " \
    libevdev \
    libinput \
    udev \
"

SRC_URI = "git://salsa.debian.org/consolation-team/consolation.git;branch=master"
SRCREV = "5ef08f0cd6ede62de14de477b74fc3611108c676"

S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${B}/consolation.service ${D}${systemd_system_unitdir}
}

SYSTEMD_SERVICE:${PN} = "consolation.service"

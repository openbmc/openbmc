SUMMARY = "copy-paste for the Linux console"
DESCRIPTION = "Consolation is a daemon that provides copy-paste and scrolling \
support to the Linux console. It is based on the libinput library and \
supports all pointer devices and settings provided by this library. Similar \
software include gpm and jamd."
HOMEPAGE = "https://salsa.debian.org/consolation-team/consolation"
SECTION = "console/utils"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73ca626e1d9048abfc7d599370650827"

DEPENDS = " \
    libevdev \
    libinput \
    udev \
"

SRC_URI = "git://salsa.debian.org/consolation-team/consolation.git"
SRCREV = "4581eaece6e49fa2b687efbdbe23b2de452e7902"

S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd

do_install_append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${B}/consolation.service ${D}${systemd_system_unitdir}
}

SYSTEMD_SERVICE_${PN} = "consolation.service"

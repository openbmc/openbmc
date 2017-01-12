SUMMARY = "Phosphor OpenBMC MBOX Daemon"
DESCRIPTION = "Phosphor OpenBMC MBOX Daemon"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} = "org.openbmc.mboxd.service"

PROVIDES += "mboxd"
DEPENDS += "systemd"
DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "libsystemd"

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/cyrilbur-ibm/mboxbridge;rebaseable=1"
SRC_URI += "file://99-aspeed-mbox.rules"

SRCREV="0c6980c8819e91e237be4a09711061736b087f0f"

# This is how linux-libc-headers says to include custom uapi headers
CFLAGS_append = "-I${S}"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

RDEPENDS_${PN} += "udev"

do_install_append() {
    install -d ${D}/lib/udev/rules.d
    install -m 0644 ${WORKDIR}/99-aspeed-mbox.rules ${D}/lib/udev/rules.d
}

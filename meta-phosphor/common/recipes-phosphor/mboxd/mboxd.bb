SUMMARY = "Phosphor OpenBMC MBOX Daemon"
DESCRIPTION = "Phosphor OpenBMC MBOX Daemon"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} = "org.openbmc.mboxd.service"

PROVIDES += "mboxd"
DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/cyrilbur-ibm/mboxbridge;rebaseable=1"
SRCREV="0c6980c8819e91e237be4a09711061736b087f0f"

# This is how linux-libc-headers says to include custom uapi headers
CFLAGS_append = "-I${S}"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"

inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

SYSTEMD_SERVICE_${PN} = "btbridged.service"

inherit obmc-phosphor-host-ipmi-hw

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/btbridge file://makefile.patch"
SRCREV="5cb9032815aa9d2866229caac4943520d5f40dd8"

# This is how linux-libc-headers says to include custom uapi headers
EXTRA_OEMAKE_append = "CFLAGS=-I${STAGING_KERNEL_DIR}/include/uapi"
DEPENDS += "virtual/kernel"

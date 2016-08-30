SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"

inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-c-daemon

DBUS_SERVICE_${PN} = "org.openbmc.HostIpmi.service"

inherit obmc-phosphor-host-ipmi-hw

DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/btbridge"
SRCREV="39b3700766d851009258544aa0f75365f024c597"

# This is how linux-libc-headers says to include custom uapi headers
EXTRA_OEMAKE_append = "CFLAGS=-I${STAGING_KERNEL_DIR}/include/uapi"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

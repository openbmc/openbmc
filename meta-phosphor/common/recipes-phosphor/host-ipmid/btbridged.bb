SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"

inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

SYSTEMD_SERVICE_${PN} = "btbridged.service"

inherit obmc-phosphor-host-ipmi-hw

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/btbridge"
SRCREV="c0ee3595f9a4784c7cf7ee60fd247d9712ec7e6b"

# This is how linux-libc-headers says to include custom uapi headers
EXTRA_OEMAKE_append = "CFLAGS=-I${STAGING_KERNEL_DIR}/include/uapi"
DEPENDS += "virtual/kernel"

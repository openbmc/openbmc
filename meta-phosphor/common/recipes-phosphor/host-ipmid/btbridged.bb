SUMMARY = "Phosphor OpenBMC BT to DBUS"
DESCRIPTION = "Phosphor OpenBMC BT to DBUS."
PR = "r1"

inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

SYSTEMD_SERVICE_${PN} = "btbridged.service"

inherit obmc-phosphor-host-ipmi-hw

S = "${WORKDIR}/git"
SRC_URI += "git://github.com/openbmc/btbridge file://makefile.patch"
SRCREV="e71c7941a35949c0b93ce5e7864ab35447d30df2"

# This is how linux-libc-headers says to include custom uapi headers
EXTRA_OEMAKE_append = "CFLAGS=-I${STAGING_KERNEL_DIR}/include/uapi"
DEPENDS += "virtual/kernel"

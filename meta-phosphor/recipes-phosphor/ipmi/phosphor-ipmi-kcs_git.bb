SUMMARY = "Phosphor OpenBMC KCS to DBUS"
DESCRIPTION = "Phosphor OpenBMC KCS to DBUS."
PR = "r1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1beb00e508e89da1ed2a541934f28c0"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

PV = "1.0+git${SRCPV}"

KCS_DEVICE ?= "ipmi-kcs3"

DBUS_SERVICE_${PN} = "org.openbmc.HostIpmi.service"
SYSTEMD_SUBSTITUTIONS += "KCS_DEVICE:${KCS_DEVICE}:${DBUS_SERVICE_${PN}}"

PROVIDES += "virtual/obmc-host-ipmi-hw"
RPROVIDES_${PN} += "virtual-obmc-host-ipmi-hw"
RRECOMMENDS_${PN} += "phosphor-ipmi-host"

DEPENDS += " \
        autoconf-archive-native \
        systemd \
        "
RDEPENDS_${PN} += "libsystemd"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/kcsbridge.git;protocol=https"
SRCREV = "a1c50751b57a73ee38469c03960831ace72bc860"

# This is how linux-libc-headers says to include custom uapi headers
CFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include/uapi"
do_configure[depends] += "virtual/kernel:do_shared_workdir"

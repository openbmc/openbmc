SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit autotools obmc-phosphor-utils pkgconfig pythonnative
inherit systemd

SRC_URI += "git://github.com/openbmc/openpower-proc-control"
SRCREV = "16ab00cb9383b17b8dd033a1cb300e2a013d55b1"

DEPENDS += " \
        autoconf-archive-native \
        phosphor-logging \
        phosphor-dbus-interfaces \
        openpower-dbus-interfaces \
        "
RDEPENDS_${PN} += "pdbg"

TEMPLATE = "pcie-poweroff@.service"
INSTANCE_FORMAT = "pcie-poweroff@{}.service"
INSTANCES = "${@compose_list(d, 'INSTANCE_FORMAT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${TEMPLATE} ${INSTANCES}"

SYSTEMD_SERVICE_${PN} +=  " \
                         xyz.openbmc_project.Control.Host.NMI.service \
                         nmi.service \
                         "

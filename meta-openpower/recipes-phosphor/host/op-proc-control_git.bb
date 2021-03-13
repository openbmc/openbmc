SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit autotools obmc-phosphor-utils pkgconfig
inherit systemd

SRC_URI += "git://github.com/openbmc/openpower-proc-control"
SRCREV = "26c4d763d3dcddc3638a960973cefef506b3735b"

DEPENDS += " \
        autoconf-archive-native \
        phosphor-logging \
        phosphor-dbus-interfaces \
        libgpiod \
        "

# For libpdbg, provided by the pdbg package
DEPENDS += "pdbg"

TEMPLATE = "pcie-poweroff@.service"
INSTANCE_FORMAT = "pcie-poweroff@{}.service"
INSTANCES = "${@compose_list(d, 'INSTANCE_FORMAT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "${TEMPLATE} ${INSTANCES}"

SYSTEMD_SERVICE_${PN} +=  " \
                         xyz.openbmc_project.Control.Host.NMI.service \
                         op-stop-instructions@.service \
                         op-cfam-reset.service \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'set-spi-mux.service', '', d)} \
                         op-continue-mpreboot@.service \
                         op-enter-mpreboot@.service \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'phal-reinit-devtree.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'proc-pre-poweroff@.service', '', d)} \
                         "

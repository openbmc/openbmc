SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit meson obmc-phosphor-utils pkgconfig
inherit systemd

SRC_URI += "git://github.com/openbmc/openpower-proc-control"
SRCREV = "61c8757c6b81d61ae158af002810cdbe7cdae7e2"

DEPENDS += " \
        phosphor-logging \
        phosphor-dbus-interfaces \
        libgpiod \
        "

EXTRA_OEMESON += "-Dtests=disabled"

# For libpdbg, provided by the pdbg package
DEPENDS += "pdbg"

TEMPLATE = "pcie-poweroff@.service"
INSTANCE_FORMAT = "pcie-poweroff@{}.service"
INSTANCES = "${@compose_list(d, 'INSTANCE_FORMAT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${TEMPLATE} ${INSTANCES}"

SYSTEMD_SERVICE:${PN} +=  " \
                         xyz.openbmc_project.Control.Host.NMI.service \
                         op-stop-instructions@.service \
                         op-cfam-reset.service \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'set-spi-mux.service', '', d)} \
                         op-continue-mpreboot@.service \
                         op-enter-mpreboot@.service \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'phal-reinit-devtree.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'proc-pre-poweroff@.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'op-reset-host-check@.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'op-reset-host-clear.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'phal-import-devtree@.service', '', d)} \
                         ${@bb.utils.contains('OBMC_MACHINE_FEATURES', 'phal', 'phal-export-devtree@.service', '', d)} \
                         "

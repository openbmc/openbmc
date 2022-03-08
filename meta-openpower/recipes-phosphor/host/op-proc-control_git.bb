SUMMARY = "OpenPower procedure control"
DESCRIPTION = "Provides procedures that run against the host chipset"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

inherit meson obmc-phosphor-utils pkgconfig
inherit systemd

SRC_URI += "git://github.com/openbmc/openpower-proc-control;branch=master;protocol=https"
SRCREV = "d7469407ec4d45cc1c1bd3704be18c7ba7ecdcda"

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
        op-cfam-reset.service \
        op-continue-mpreboot@.service \
        op-enter-mpreboot@.service \
        op-stop-instructions@.service \
        xyz.openbmc_project.Control.Host.NMI.service \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-reset-host-check@.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'op-reset-host-clear.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-export-devtree@.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-import-devtree@.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-reinit-devtree.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'proc-pre-poweroff@.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'set-spi-mux.service', '', d)} \
        ${@bb.utils.contains('MACHINE_FEATURES', 'phal', 'phal-create-boottime-guard-indicator.service', '', d)} \
        "

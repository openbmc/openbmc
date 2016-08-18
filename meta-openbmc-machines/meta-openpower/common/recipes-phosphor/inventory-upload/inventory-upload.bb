SUMMARY = "Inventory upload"
DESCRIPTION = "Inventory upload."
HOMEPAGE = "http://github.com/openbmc/openpower-inventory-upload"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit setuptools
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += " \
        python-dbus \
        python-pygobject \
        pyphosphor \
        dtc \
        "

SRC_URI += "git://github.com/openbmc/openpower-inventory-upload"

SRCREV = "b07de872ac18710dbf0d393a206e2598c6580f46"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "op-inventory-upload@.service"
SYSTEMD_GENLINKS_${PN} += "../op-inventory-upload@.service:obmc-chassis-start@[0].target.wants/op-inventory-upload@[0].service:OBMC_CHASSIS_INSTANCES"

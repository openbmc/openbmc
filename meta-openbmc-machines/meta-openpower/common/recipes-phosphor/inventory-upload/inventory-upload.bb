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

TMPL = "op-inventory-upload@.service"
INSTFMT = "op-inventory-upload@{0}.service"
TGTFMT = "obmc-host-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"

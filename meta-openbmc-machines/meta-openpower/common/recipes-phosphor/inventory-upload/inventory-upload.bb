SUMMARY = "Inventory upload"
DESCRIPTION = "Inventory upload."
HOMEPAGE = "http://github.com/openbmc/openpower-inventory-upload"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools

RDEPENDS_${PN} += " \
        python-dbus \
        python-pygobject \
        pyphosphor \
        dtc \
        "

SRC_URI += "git://github.com/openbmc/openpower-inventory-upload"

SRCREV = "867e058dd246739ab9c93df64e82b7526f42d67f"

S = "${WORKDIR}/git"

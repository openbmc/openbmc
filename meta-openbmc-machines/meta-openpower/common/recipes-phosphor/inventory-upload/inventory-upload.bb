SUMMARY = "Inventory upload"
DESCRIPTION = "Inventory upload."
HOMEPAGE = "http://github.com/openbmc/openpower-inventory-upload"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit setuptools

RDEPENDS_${PN} += " \
        pyphosphor \
        dtc \
        "

SRC_URI += "git://github.com/openbmc/openpower-inventory-upload"

SRCREV = "5909e7859a1a56bdc902cf774093d9fae29612ea"

S = "${WORKDIR}/git"

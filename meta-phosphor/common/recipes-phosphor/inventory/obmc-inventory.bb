SUMMARY = "Phosphor Inventory Generation"
DESCRIPTION = "Generates inventory data from the machine readable workbook"
PR = "r1"

S = "${WORKDIR}/git"

RPROVIDES_${PN} = "virtual-system-inventory-data"

inherit allarch
inherit obmc-phosphor-license

DEPENDS += "mrw-native mrw-api-native"

SRC_URI += "git://github.com/openbmc/phosphor-mrw-tools"
SRCREV = "ab015d7e2a2eb87eab2ca7d731ebcb7a873442e9"

FILES_${PN} += "${datadir}/inventory"

do_compile() {
    ${STAGING_BINDIR_NATIVE}/perl-native/perl ${S}/inventory.pl \
        -x ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml -o inventory.json
}

do_install() {
    install -d ${D}${datadir}/inventory
    install -m 0644 inventory.json ${D}${datadir}/inventory/inventory.json
}

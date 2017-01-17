SUMMARY = "Witherspoon IPMI to DBus Inventory mapping."
PR = "r1"

inherit native
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-ipmi-fru-config"

do_install() {
        DEST=${D}${datadir}/phosphor-ipmi-fru
        install -d ${DEST}

        # TODO - Run an MRW script.
}

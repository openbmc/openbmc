SUMMARY = "Recipe to create AssetTag property in inventory manager"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-inventory-manager

PROVIDES += "virtual/phosphor-inventory-manager-assettag"

SRC_URI += "file://assettag.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe would provide the yaml for inventory manager to
        # create AssetTag property at startup

        install -d ${base_datadir}/events.d/
        install assettag.yaml ${base_datadir}/events.d/assettag.yaml
}

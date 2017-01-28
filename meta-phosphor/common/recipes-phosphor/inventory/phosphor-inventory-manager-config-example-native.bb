SUMMARY = "Managed inventory with Phosphor inventory manager - example"
PR = "r1"

inherit native
inherit phosphor-inventory-manager

require phosphor-inventory-manager.inc

S = "${WORKDIR}/git"

do_install() {
        SRC=${S}/example
        DEST=${D}${base_datadir}

        for f in `find $SRC -type f -printf "%P\n"`; do
                install -D ${SRC}/$f ${DEST}/$f
        done
}

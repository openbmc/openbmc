FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGES:remove:fb-nohost = "\
        ${PN}-console \
        "

RDEPENDS:${PN}-fan-control:remove:mf-fb-fanless = "\
        ${VIRTUAL-RUNTIME_obmc-fan-control} \
        phosphor-fan-monitor \
"

RDEPENDS:${PN}-inventory:remove:mf-fb-fanless = "\
        ${VIRTUAL-RUNTIME_obmc-fan-presence} \
        "

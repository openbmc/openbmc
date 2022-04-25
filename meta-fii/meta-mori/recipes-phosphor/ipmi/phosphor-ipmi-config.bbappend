FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"

# Remove unused entity-map JSON
do_install:append:mori() {
    rm ${D}${datadir}/ipmi-providers/entity-map.json
}

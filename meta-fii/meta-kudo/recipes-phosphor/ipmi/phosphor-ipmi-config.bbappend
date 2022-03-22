FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

# Remove unused entity-map JSON
do_install:append:kudo() {
    rm ${D}${datadir}/ipmi-providers/entity-map.json
}

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://fru_id.json \
            file://sensor_filter.json \
           "

FILES:${PN} += " \
                ${datadir}/ipmi-providers/fru_id.json \
                ${datadir}/ipmi-providers/sensor_filter.json \
               "

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/fru_id.json \
        ${D}${datadir}/ipmi-providers/fru_id.json
    install -m 0644 -D ${UNPACKDIR}/sensor_filter.json \
        ${D}${datadir}/ipmi-providers/sensor_filter.json
}

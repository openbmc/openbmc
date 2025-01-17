FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://sensor_filter.json \
           "

FILES:${PN} += " \
                ${datadir}/ipmi-providers/sensor_filter.json \
               "

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/sensor_filter.json \
        ${D}${datadir}/ipmi-providers/sensor_filter.json
}

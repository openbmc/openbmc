FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://gpio_defs.json \
           "

do_install:append() {
	install -d ${D}${sysconfdir}/default/obmc/gpio/
	install -m 0644 ${WORKDIR}/gpio_defs.json ${D}/${sysconfdir}/default/obmc/gpio/gpio_defs.json
}

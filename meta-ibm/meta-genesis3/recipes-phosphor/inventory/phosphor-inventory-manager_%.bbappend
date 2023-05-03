FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://inventory.yaml"

do_install:append() {
        install -D ${WORKDIR}/inventory.yaml ${D}${base_datadir}/events.d/inventory.yaml
}

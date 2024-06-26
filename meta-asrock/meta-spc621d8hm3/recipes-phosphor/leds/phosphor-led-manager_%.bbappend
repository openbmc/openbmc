FILESEXTRAPATHS:prepend:spc621d8hm3 := "${THISDIR}/${PN}:"

SRC_URI:append:spc621d8hm3 = " file://led-group-config.json"

do_install:append:spc621d8hm3() {
        install -m 0644 ${UNPACKDIR}/led-group-config.json ${D}${datadir}/phosphor-led-manager/
}

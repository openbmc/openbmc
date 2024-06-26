FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:p10bmc = " file://debug-trigger.default"

do_install:append:p10bmc() {
        # Install the defaults file
        mkdir -p ${D}${sysconfdir}/default
        install -m 0644 -T ${UNPACKDIR}/debug-trigger.default ${D}${sysconfdir}/default/debug-trigger
}

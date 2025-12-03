FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ibm-enterprise = " file://debug-trigger.default"

do_install:append:ibm-enterprise() {
        # Install the defaults file
        mkdir -p ${D}${sysconfdir}/default
        install -m 0644 -T ${UNPACKDIR}/debug-trigger.default ${D}${sysconfdir}/default/debug-trigger
}

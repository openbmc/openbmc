FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:qcom-bmc-ast2600 = " \
    file://led-group-config.json \
"

do_install:append() {
    install -m 0644 ${UNPACKDIR}/*.json ${D}${datadir}/phosphor-led-manager/
}

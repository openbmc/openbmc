FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
EXTRA_OEMESON += "-Dchassis-system-reset=enabled"

SRC_URI:append:yosemitev2 = " file://power-config-host1.json"
SRC_URI:append:yosemitev2 = " file://power-config-host2.json"
SRC_URI:append:yosemitev2 = " file://power-config-host3.json"
SRC_URI:append:yosemitev2 = " file://power-config-host4.json"

do_install:append:yosemitev2() {
    install -m 0755 -d ${D}/${datadir}/${BPN}
    install -m 0644 -D ${UNPACKDIR}/*.json \
                   ${D}/${datadir}/${BPN}/
}

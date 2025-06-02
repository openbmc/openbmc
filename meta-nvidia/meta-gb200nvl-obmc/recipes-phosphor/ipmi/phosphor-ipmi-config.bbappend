FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://dev_id.json \
                   file://channel_access.json \
                   file://channel_config.json \
                 "

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json

    install -m 0644 -D ${UNPACKDIR}/channel_access.json \
        ${D}${datadir}/ipmi-providers/channel_access.json

    install -m 0644 -D ${UNPACKDIR}/channel_config.json \
        ${D}${datadir}/ipmi-providers/channel_config.json
}

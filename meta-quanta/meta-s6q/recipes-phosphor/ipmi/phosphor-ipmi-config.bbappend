FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SRC_URI:append:s6q = " file://bond_channel_config.json"
SRC_URI:append:s6q = " file://bond_channel_access.json"

do_install:append:s6q() {
    if ${@bb.utils.contains('MACHINE_FEATURES', 'bonding', 'true', 'false', d)};
    then
        install -m 0644 -D ${UNPACKDIR}/bond_channel_access.json \
            ${D}${datadir}/ipmi-providers/channel_access.json
        install -m 0644 -D ${UNPACKDIR}/bond_channel_config.json \
            ${D}${datadir}/ipmi-providers/channel_config.json
    fi
}

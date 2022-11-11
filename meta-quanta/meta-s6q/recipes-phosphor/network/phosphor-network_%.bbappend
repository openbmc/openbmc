PACKAGECONFIG:append:s6q = " sync-mac"

FILES:${PN}:append:s6q = " ${datadir}/network/config.json"

do_install:append:s6q() {
    install -d ${D}${datadir}/network/
    if ${@bb.utils.contains('MACHINE_FEATURES', 'bonding', 'true', 'false', d)};
    then
        echo '{"bond0":"bmc","eth0":"bmc","eth1":"bmc"}' \
            > ${D}${datadir}/network/config.json
    else
        echo '{"eth0":"bmc"}' > ${D}${datadir}/network/config.json
    fi
}

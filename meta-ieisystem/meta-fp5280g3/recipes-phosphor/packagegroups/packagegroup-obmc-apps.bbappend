
RDEPENDS:${PN}-inventory:append = " \
        entity-manager \
        openpower-occ-control \
        phosphor-gpio-monitor-presence \
        "

RDEPENDS:${PN}-extras:append = " \
        srvcfg-manager \
        biosconfig-manager \
        phosphor-host-postd \
        libmctp \
        "

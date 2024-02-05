
RDEPENDS:${PN}-inventory:append = " \
        entity-manager \
        openpower-occ-control \
        phosphor-gpio-monitor-presence \
        "

RDEPENDS:${PN}-extras:append = " \
        dbus-sensors \
        srvcfg-manager \
        biosconfig-manager \
        phosphor-host-postd \
        libmctp \
        pldm \
        tzdata-core \
        "

RDEPENDS:${PN}-devtools:append = " \
        ipmitool \
        "

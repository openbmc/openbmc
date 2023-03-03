RDEPENDS:${PN}-inventory:append:ncplite = " phosphor-gpio-monitor-presence"
RDEPENDS:${PN}-chassis-state-mgmt:append:ncplite = " phosphor-state-manager-ncplite"
RDEPENDS:${PN}-extras:append:ncplite = " entity-manager \
                                         dbus-sensors \
                                         ncplite-inventory-log \
                                         ncplite-led \
                                       "

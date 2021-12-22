RDEPENDS:${PN}-extras:append:mtjade = " \
                                       webui-vue \
                                       phosphor-image-signing \
                                       phosphor-virtual-sensor \
                                       phosphor-misc-usb-ctrl \
                                       fault-monitor \
                                       id-button \
                                       psu-hotswap-reset \
                                      "

RDEPENDS:${PN}-inventory:append:mtjade = " \
                                          host-gpio-handling \
                                          dbus-sensors \
                                          entity-manager \
                                         "

RDEPENDS:${PN}-extras:remove:mtjade = " phosphor-hwmon"
VIRTUAL-RUNTIME_obmc-sensors-hwmon ?= "dbus-sensors"
RDEPENDS:${PN}-extras:append:mtjade = " phosphor-virtual-sensor"

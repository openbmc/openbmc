POWER_SERVICE_PACKAGES_FP5280G2 = " \
                                    phosphor-power-monitor \
                                    phosphor-power-systemd-links-monitor \
                                    phosphor-power-utils \
                                    phosphor-power \
                                  "

RDEPENDS:${PN}-inventory:append:fp5280g2 = " \
                                             openpower-fru-vpd \
                                             openpower-occ-control \
                                             phosphor-cooling-type \
                                             phosphor-gpio-monitor-presence \
                                            "

RDEPENDS:${PN}-extras:append:fp5280g2 = " \
                                          ${POWER_SERVICE_PACKAGES_FP5280G2} \
                                          phosphor-led-manager \
                                          pldm \
                                          libmctp \
                                          phosphor-fp5280g2-psu-update \
                                          phosphor-psu-software-manager \
                                          biosconfig-manager \
                                          srvcfg-manager \
                                          phosphor-sel-logger \
                                          tzdata-core \
                                          webui-vue \
                                         "


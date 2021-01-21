POWER_SERVICE_PACKAGES_FP5280G2 = " \
                                    phosphor-power-monitor \
                                    phosphor-power-systemd-links-monitor \
                                    phosphor-power-utils \
                                    phosphor-power \
                                    phosphor-power-psu-monitor \
                                  "

RDEPENDS_${PN}-inventory_append_fp5280g2 = " \
                                             openpower-fru-vpd \
                                             openpower-occ-control \
                                             phosphor-cooling-type \
                                             virtual/obmc-gpio-presence \
                                            "

RDEPENDS_${PN}-extras_append_fp5280g2 = " \
                                          ${POWER_SERVICE_PACKAGES_FP5280G2} \
                                          phosphor-led-manager \
                                          pldm \
                                          mctp \
                                          phosphor-fp5280g2-psu-update \
                                          phosphor-psu-software-manager \
                                          webui-vue \
                                         "


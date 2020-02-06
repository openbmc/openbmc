POWER_SERVICE_PACKAGES_AC_SERVER = " \
    phosphor-power-monitor \
    phosphor-power-systemd-links-monitor \
    phosphor-power-sequencer \
    phosphor-power-systemd-links-sequencer \
    phosphor-power-utils \
    phosphor-power \
"

POWER_SERVICE_PACKAGES_RAINIER = " \
    ${POWER_SERVICE_PACKAGES_AC_SERVER} \
    phosphor-power-regulators \
    phosphor-power-psu-monitor \
"

RDEPENDS_${PN}-inventory_append_ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_rainier = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_mihawk = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence id-button phosphor-cooling-type"
RDEPENDS_${PN}-fan-control_append_ibm-ac-server = " witherspoon-fan-watchdog"
RDEPENDS_${PN}-extras_append_ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync phosphor-webui"
RDEPENDS_${PN}-extras_append_rainier = " ${POWER_SERVICE_PACKAGES_RAINIER} phosphor-webui"
RDEPENDS_${PN}-extras_append_mihawk = " phosphor-webui phosphor-image-signing"
RDEPENDS_${PN}-extras_remove_rainier = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS_${PN}-extras_remove_swift = "obmc-ikvm"
RDEPENDS_${PN}-extras_remove_witherspoon-tacoma = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS_${PN}-logging_remove_rainier = "ibm-logging"
RDEPENDS_${PN}-extras_append_rainier = " pldm"
RDEPENDS_${PN}-extras_append_witherspoon-128 = " pldm"
RDEPENDS_${PN}-extras_append_witherspoon-tacoma = " pldm"

${PN}-software-extras_append_ibm-ac-server = " phosphor-software-manager-sync"

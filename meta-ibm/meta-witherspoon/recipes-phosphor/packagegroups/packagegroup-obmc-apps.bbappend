RDEPENDS_${PN}-inventory_append_ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_mihawk = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence id-button phosphor-cooling-type"
RDEPENDS_${PN}-fan-control_append_ibm-ac-server = " witherspoon-fan-watchdog"
RDEPENDS_${PN}-extras_append_ibm-ac-server = " witherspoon-pfault-analysis witherspoon-power-supply-sync phosphor-webui"
RDEPENDS_${PN}-extras_append_mihawk = " phosphor-webui phosphor-image-signing"

${PN}-software-extras_append_ibm-ac-server = " phosphor-software-manager-sync"

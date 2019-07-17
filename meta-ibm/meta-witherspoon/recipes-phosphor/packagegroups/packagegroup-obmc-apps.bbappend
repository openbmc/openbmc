RDEPENDS_${PN}-inventory_append_ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS_${PN}-fan-control_append_ibm-ac-server = " witherspoon-fan-watchdog"
RDEPENDS_${PN}-extras_append_ibm-ac-server = " witherspoon-pfault-analysis witherspoon-power-supply-sync phosphor-webui"

${PN}-software-extras_append_ibm-ac-server = " phosphor-software-manager-sync"

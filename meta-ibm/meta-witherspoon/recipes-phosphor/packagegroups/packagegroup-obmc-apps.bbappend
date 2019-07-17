RDEPENDS_${PN}-inventory += "openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS_${PN}-fan-control += "witherspoon-fan-watchdog"
RDEPENDS_${PN}-extras += "witherspoon-pfault-analysis witherspoon-power-supply-sync phosphor-webui"

${PN}-software-extras_append_witherspoon = " phosphor-software-manager-sync"
${PN}-software-extras_append_swift = " phosphor-software-manager-sync"

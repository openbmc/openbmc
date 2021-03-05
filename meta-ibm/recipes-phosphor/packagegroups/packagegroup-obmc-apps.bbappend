POWER_SERVICE_PACKAGES_AC_SERVER = " \
    phosphor-power-monitor \
    phosphor-power-systemd-links-monitor \
    phosphor-power-sequencer \
    phosphor-power-systemd-links-sequencer \
    phosphor-power-utils \
    phosphor-power \
"

# Rainier does not need/want the old PSU monitor
POWER_SERVICE_PACKAGES_RAINIER = " \
    phosphor-power-sequencer \
    phosphor-power-systemd-links-sequencer \
    phosphor-power-utils \
    phosphor-power \
    phosphor-power-regulators \
    phosphor-power-psu-monitor \
"

EXTRA_IBM_LOGGING_PKGS = ""
EXTRA_IBM_LOGGING_PKGS_witherspoon = "ibm-logging"
EXTRA_IBM_LOGGING_PKGS_witherspoon-tacoma = ""
EXTRA_IBM_LOGGING_PKGS_mihawk = "ibm-logging"

RDEPENDS_${PN}-inventory_append_ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_rainier = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_mihawk = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence id-button phosphor-cooling-type"
RDEPENDS_${PN}-fan-control_append_ibm-ac-server = " fan-watchdog"
RDEPENDS_${PN}-fan-control_append_rainier = " fan-watchdog sensor-monitor"
RDEPENDS_${PN}-extras_append_ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync phosphor-webui"
RDEPENDS_${PN}-extras_append_rainier = " ${POWER_SERVICE_PACKAGES_RAINIER} webui-vue dbus-sensors phosphor-virtual-sensor"
RDEPENDS_${PN}-extras_append_mihawk = " phosphor-webui phosphor-image-signing wistron-ipmi-oem ${POWER_SERVICE_PACKAGES_AC_SERVER}"
RDEPENDS_${PN}-extras_append_witherspoon-tacoma = " pldm srvcfg-manager webui-vue biosconfig-manager phosphor-post-code-manager phosphor-host-postd"

RDEPENDS_${PN}-extras_remove_rainier = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS_${PN}-extras_remove_swift = "obmc-ikvm"
RDEPENDS_${PN}-extras_remove_witherspoon-tacoma = "obmc-ikvm liberation-fonts uart-render-controller phosphor-webui"
RDEPENDS_${PN}-logging_append = " ${EXTRA_IBM_LOGGING_PKGS}"
RDEPENDS_${PN}-extras_append_rainier = " pldm openpower-hw-diags srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd"
RDEPENDS_${PN}-leds_remove_rainier = "phosphor-led-manager-faultmonitor"
RDEPENDS_${PN}-leds_remove_witherspoon-tacoma = "phosphor-led-manager-faultmonitor"

${PN}-software-extras_append_ibm-ac-server = " phosphor-software-manager-sync"

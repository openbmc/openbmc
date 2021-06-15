POWER_SERVICE_PACKAGES_AC_SERVER = " \
    phosphor-power-monitor \
    phosphor-power-systemd-links-monitor \
    phosphor-power-sequencer \
    phosphor-power-systemd-links-sequencer \
    phosphor-power-utils \
    phosphor-power \
"

# P10 does not need/want the old PSU monitor
POWER_SERVICE_PACKAGES_P10 = " \
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
RDEPENDS_${PN}-inventory_append_p10bmc = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence"
RDEPENDS_${PN}-inventory_append_mihawk = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence id-button phosphor-cooling-type"
RDEPENDS_${PN}-fan-control_append_ibm-ac-server = " fan-watchdog"
RDEPENDS_${PN}-fan-control_append_p10bmc = " fan-watchdog sensor-monitor"
RDEPENDS_${PN}-extras_append_ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync phosphor-webui"
RDEPENDS_${PN}-extras_append_p10bmc = " ${POWER_SERVICE_PACKAGES_P10} webui-vue dbus-sensors phosphor-virtual-sensor kexec-tools makedumpfile kdump vmcore-dmesg"
RDEPENDS_${PN}-extras_append_mihawk = " phosphor-webui phosphor-image-signing wistron-ipmi-oem ${POWER_SERVICE_PACKAGES_AC_SERVER}"
RDEPENDS_${PN}-extras_append_witherspoon-tacoma = " pldm srvcfg-manager webui-vue biosconfig-manager phosphor-post-code-manager phosphor-host-postd kexec-tools makedumpfile kdump vmcore-dmesg"

RDEPENDS_${PN}-extras_remove_p10bmc = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS_${PN}-host-state-mgmt_remove_p10bmc = "checkstop-monitor"
RDEPENDS_${PN}-extras_remove_swift = "obmc-ikvm"
RDEPENDS_${PN}-extras_remove_witherspoon-tacoma = "obmc-ikvm liberation-fonts uart-render-controller phosphor-webui"
RDEPENDS_${PN}-logging_append = " ${EXTRA_IBM_LOGGING_PKGS}"
RDEPENDS_${PN}-extras_append_p10bmc = " pldm openpower-hw-diags srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd"
RDEPENDS_${PN}-leds_remove_witherspoon-tacoma = "phosphor-led-manager-faultmonitor"

${PN}-software-extras_append_ibm-ac-server = " phosphor-software-manager-sync"

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
    phosphor-power-control \
    phosphor-power-utils \
    phosphor-power \
    phosphor-power-regulators \
    phosphor-power-psu-monitor \
"

EXTRA_IBM_LOGGING_PKGS = ""
EXTRA_IBM_LOGGING_PKGS:witherspoon = "ibm-logging"
EXTRA_IBM_LOGGING_PKGS:witherspoon-tacoma = ""
EXTRA_IBM_LOGGING_PKGS:mihawk = "ibm-logging"
EXTRA_IBM_LOGGING_PKGS:p10bmc = " \
    python3-sbe-log-parsers \
"

RDEPENDS:${PN}-inventory:append:ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type virtual/obmc-gpio-presence"
RDEPENDS:${PN}-inventory:append:p10bmc = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence"
RDEPENDS:${PN}-inventory:append:mihawk = " openpower-fru-vpd openpower-occ-control virtual/obmc-gpio-presence id-button phosphor-cooling-type"
RDEPENDS:${PN}-fan-control:append:ibm-ac-server = " fan-watchdog"
RDEPENDS:${PN}-fan-control:append:p10bmc = " fan-watchdog sensor-monitor"
RDEPENDS:${PN}-extras:append:ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync"
RDEPENDS:${PN}-extras:append:p10bmc = " ${POWER_SERVICE_PACKAGES_P10} webui-vue dbus-sensors phosphor-virtual-sensor"
RDEPENDS:${PN}-extras:append:p10bmc = " pldm openpower-hw-diags srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd debug-trigger libmctp"
RDEPENDS:${PN}-extras:append:p10bmc = " gdbserver strace opkg"
RDEPENDS:${PN}-extras:append:mihawk = " phosphor-webui phosphor-image-signing wistron-ipmi-oem ${POWER_SERVICE_PACKAGES_AC_SERVER}"
RDEPENDS:${PN}-extras:append:witherspoon-tacoma = " pldm srvcfg-manager webui-vue biosconfig-manager phosphor-post-code-manager phosphor-host-postd kexec-tools makedumpfile kdump vmcore-dmesg debug-trigger"

RDEPENDS:${PN}-extras:remove:p10bmc = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS:${PN}-host-state-mgmt:remove:p10bmc = "checkstop-monitor"
RDEPENDS:${PN}-extras:remove:swift = "obmc-ikvm"
RDEPENDS:${PN}-extras:remove:witherspoon-tacoma = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS:${PN}-logging:append = " ${EXTRA_IBM_LOGGING_PKGS}"
RDEPENDS:${PN}-leds:remove:witherspoon-tacoma = "phosphor-led-manager-faultmonitor"

${PN}-software-extras:append:ibm-ac-server = " phosphor-software-manager-sync"
${PN}-software-extras:append:p10bmc = " phosphor-software-manager-usb"
${PN}-software-extras:append:p10bmc = " phosphor-software-manager-side-switch"

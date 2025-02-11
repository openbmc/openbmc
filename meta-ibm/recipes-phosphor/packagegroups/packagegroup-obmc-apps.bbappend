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
    phosphor-power-systemd-links-regulators \
    phosphor-power-psu-monitor \
"

EXTRA_IBM_LOGGING_PKGS = ""
EXTRA_IBM_LOGGING_PKGS:witherspoon = ""
EXTRA_IBM_LOGGING_PKGS:witherspoon-tacoma = ""
EXTRA_IBM_LOGGING_PKGS:p10bmc = " \
    python3-sbe-log-parsers \
    hostboot-pel-parsers \
"

RDEPENDS:${PN}-inventory:append:ibm-ac-server = " openpower-fru-vpd openpower-occ-control phosphor-cooling-type phosphor-gpio-monitor-presence"
RDEPENDS:${PN}-inventory:append:p10bmc = " openpower-fru-vpd openpower-occ-control phosphor-gpio-monitor-presence entity-manager"
RDEPENDS:${PN}-inventory:append:witherspoon-tacoma = " entity-manager"
RDEPENDS:${PN}-inventory:remove:huygens = " openpower-occ-control"

RDEPENDS:${PN}-fan-control:append:ibm-ac-server = " fan-watchdog"
RDEPENDS:${PN}-fan-control:append:p10bmc = " fan-watchdog"

RDEPENDS:${PN}-extras:append:ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync"
RDEPENDS:${PN}-extras:append:p10bmc = " ${POWER_SERVICE_PACKAGES_P10} dbus-sensors phosphor-virtual-sensor"
RDEPENDS:${PN}-extras:append:p10bmc = " pldm openpower-hw-diags srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd debug-trigger libmctp"
RDEPENDS:${PN}-extras:remove:p10bmc = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS:${PN}-extras:append:witherspoon-tacoma = " pldm srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd kexec-tools makedumpfile kdump vmcore-dmesg debug-trigger libmctp"
RDEPENDS:${PN}-extras:remove:witherspoon-tacoma = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS:${PN}-extras:append:sbp1 = " phosphor-ipmi-ipmb "

RDEPENDS:${PN}-software:append:ibm-ac-server = " phosphor-software-manager-sync"
RDEPENDS:${PN}-software:append:p10bmc = " phosphor-software-manager-usb"
RDEPENDS:${PN}-software:append:p10bmc = " phosphor-software-manager-side-switch"
RDEPENDS:${PN}-software:append:system1 = " phosphor-software-manager-side-switch"

RDEPENDS:${PN}-host-state-mgmt:remove:p10bmc = "checkstop-monitor"
RDEPENDS:${PN}-logging:append = " ${EXTRA_IBM_LOGGING_PKGS}"
RDEPENDS:${PN}-leds:remove:witherspoon-tacoma = "phosphor-led-manager-faultmonitor"
RDEPENDS:${PN}-devtools:remove:witherspoon = "rsync"

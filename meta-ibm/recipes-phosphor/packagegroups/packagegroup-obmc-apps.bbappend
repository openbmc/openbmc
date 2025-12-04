POWER_SERVICE_PACKAGES_AC_SERVER = " \
    phosphor-power-monitor \
    phosphor-power-systemd-links-monitor \
    phosphor-power-sequencer \
    phosphor-power-systemd-links-sequencer \
    phosphor-power-utils \
    phosphor-power \
"

# P10 does not need/want the old PSU monitor
POWER_SERVICE_PACKAGES_IBM_ENTERPRISE = " \
    phosphor-power-control \
    phosphor-power-utils \
    phosphor-power \
    phosphor-power-regulators \
    phosphor-power-systemd-links-regulators \
    phosphor-power-psu-monitor \
"

# These are the huygens-specific power service packages.
POWER_SERVICE_PACKAGES_HUYGENS = " \
    phosphor-power-chassis \
"

EXTRA_IBM_LOGGING_PKGS = ""
EXTRA_IBM_LOGGING_PKGS:witherspoon = ""
EXTRA_IBM_LOGGING_PKGS:ibm-enterprise = " \
    python3-sbe-log-parsers \
    hostboot-pel-parsers \
"

RDEPENDS:${PN}-inventory:append:ibm-ac-server = " openpower-fru-vpd phosphor-cooling-type phosphor-gpio-monitor-presence"
RDEPENDS:${PN}-inventory:append:ibm-enterprise = " openpower-fru-vpd openpower-occ-control phosphor-gpio-monitor-presence entity-manager"
RDEPENDS:${PN}-inventory:remove:huygens = " openpower-occ-control"

RDEPENDS:${PN}-fan-control:append:ibm-ac-server = " fan-watchdog"
RDEPENDS:${PN}-fan-control:append:ibm-enterprise = " fan-watchdog"
RDEPENDS:${PN}-fan-control:append:sbp1 = " fan-watchdog phosphor-fan-sensor-monitor"

RDEPENDS:${PN}-extras:append:ibm-ac-server = " ${POWER_SERVICE_PACKAGES_AC_SERVER} witherspoon-power-supply-sync"
RDEPENDS:${PN}-extras:append:ibm-enterprise = " ${POWER_SERVICE_PACKAGES_IBM_ENTERPRISE} dbus-sensors phosphor-virtual-sensor"
RDEPENDS:${PN}-extras:append:ibm-enterprise = " pldm openpower-hw-diags srvcfg-manager biosconfig-manager phosphor-post-code-manager phosphor-host-postd debug-trigger libmctp"
RDEPENDS:${PN}-extras:remove:ibm-enterprise = "obmc-ikvm liberation-fonts uart-render-controller"
RDEPENDS:${PN}-extras:append:sbp1 = " phosphor-ipmi-ipmb "
RDEPENDS:${PN}-extras:append:huygens = " ${POWER_SERVICE_PACKAGES_HUYGENS}"

RDEPENDS:${PN}-software:append:ibm-ac-server = " phosphor-software-manager-sync"
RDEPENDS:${PN}-software:append:ibm-enterprise = " phosphor-software-manager-usb"
RDEPENDS:${PN}-software:append:ibm-enterprise = " phosphor-software-manager-side-switch"
RDEPENDS:${PN}-software:append:system1 = " phosphor-software-manager-side-switch"

RDEPENDS:${PN}-host-state-mgmt:remove:ibm-enterprise = "checkstop-monitor"
RDEPENDS:${PN}-logging:append = " ${EXTRA_IBM_LOGGING_PKGS}"
RDEPENDS:${PN}-logging:append = " phosphor-logging-test"
RDEPENDS:${PN}-leds:remove:sbp1 = "phosphor-led-manager-faultmonitor"
RDEPENDS:${PN}-devtools:remove:witherspoon = "rsync"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

# To delay turning off host when fan sensors are not detected yet
# To use CurrentHostState to decide host state for phosphor-fan-monitor

PACKAGECONFIG:append = " delay-host-control monitor-use-host-state"
PACKAGECONFIG[delay-host-control] = "-Ddelay-host-control=150"
PACKAGECONFIG[monitor-use-host-state] = "-Dmonitor-use-host-state=enabled"

COMMON_OVERRIDE_CONF = "phosphor-fan-override.conf"

SRC_URI:append = " \
                  file://${COMMON_OVERRIDE_CONF} \
                 "

SYSTEMD_OVERRIDE:${PN}-control += "${COMMON_OVERRIDE_CONF}:${TMPL_CONTROL}.d/${COMMON_OVERRIDE_CONF}"
SYSTEMD_OVERRIDE:${PN}-monitor += "${COMMON_OVERRIDE_CONF}:${TMPL_MONITOR}.d/${COMMON_OVERRIDE_CONF}"
SYSTEMD_OVERRIDE:${PN}-presence-tach += "${COMMON_OVERRIDE_CONF}:${TMPL_TACH}.d/${COMMON_OVERRIDE_CONF}"

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

# To delay turning off host when fan sensors are not detected yet
# To use CurrentHostState to decide host state for phosphor-fan-monitor

PACKAGECONFIG:append = " delay-host-control monitor-use-host-state"
PACKAGECONFIG[delay-host-control] = "-Ddelay-host-control=150"
PACKAGECONFIG[monitor-use-host-state] = "-Dmonitor-use-host-state=enabled"

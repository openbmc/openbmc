FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " \
                  file://phosphor-fan-control@.service \
                  file://phosphor-fan-monitor@.service \
                  file://phosphor-fan-presence-tach@.service \
                 "

# To delay turning off host when fan sensors are not detected yet
# To use CurrentHostState to decide host state for phosphor-fan-monitor

PACKAGECONFIG:append = " delay-host-control monitor-use-host-state"
PACKAGECONFIG[delay-host-control] = "-Ddelay-host-control=150"
PACKAGECONFIG[monitor-use-host-state] = "-Dmonitor-use-host-state=enabled"

SRC_URI += " \
            file://0001-Change-count_state_before_target-behavior.patch \
           "

do_install:append () {
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}
        rm ${D}${bindir}/fanctl
}

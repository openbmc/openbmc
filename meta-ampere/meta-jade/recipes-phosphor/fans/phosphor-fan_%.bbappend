FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"
FILESEXTRAPATHS:append := "${THISDIR}/${PN}/${MACHINE}:"

RDEPENDS:${PN}-monitor += "bash"

PACKAGECONFIG:append = " json"

SRC_URI:append = " file://events.json \
                  file://fans.json \
                  file://groups.json \
                  file://zones.json \
                  file://monitor.json \
                  file://presence.json \
                  file://phosphor-fan-control@.service \
                  file://phosphor-fan-monitor@.service \
                  file://ampere_set_fan_max_speed.sh \
                "

FILES:${PN}-monitor += " \
                        ${bindir}/ampere_set_fan_max_speed.sh \
                       "

do_configure:prepend() {
        mkdir -p ${S}/control/config_files/${MACHINE}
        cp ${UNPACKDIR}/events.json ${S}/control/config_files/${MACHINE}/events.json
        cp ${UNPACKDIR}/fans.json ${S}/control/config_files/${MACHINE}/fans.json
        cp ${UNPACKDIR}/groups.json ${S}/control/config_files/${MACHINE}/groups.json
        cp ${UNPACKDIR}/zones.json ${S}/control/config_files/${MACHINE}/zones.json

        mkdir -p ${S}/monitor/config_files/${MACHINE}
        cp ${UNPACKDIR}/monitor.json ${S}/monitor/config_files/${MACHINE}/config.json

        mkdir -p ${S}/presence/config_files/${MACHINE}
        cp ${UNPACKDIR}/presence.json ${S}/presence/config_files/${MACHINE}/config.json
}

do_install:append() {
  install -d ${D}${bindir}
  install -m 0755 ${UNPACKDIR}/ampere_set_fan_max_speed.sh ${D}${bindir}/ampere_set_fan_max_speed.sh
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/phosphor-fan-monitor@.service ${D}${systemd_system_unitdir}
  install -m 0644 ${UNPACKDIR}/phosphor-fan-control@.service ${D}${systemd_system_unitdir}
}



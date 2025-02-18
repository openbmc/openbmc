FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " json"

SRC_URI:append = " \
                  file://events.json \
                  file://fans.json \
                  file://groups_1p.json \
                  file://groups_2p.json \
                  file://zones.json \
                  file://monitor.json \
                  file://presence.json \
                 "

COMPAT_NAME = "com.ampere.Hardware.Chassis.Model.MtMitchell"

CONTROL_CONFIGS = "events.json fans.json zones.json groups_2p.json groups_1p.json"

do_install:append () {
        # datadir = /usr/share
        install -d ${D}${datadir}/phosphor-fan-presence/control/${COMPAT_NAME}
        install -d ${D}${datadir}/phosphor-fan-presence/monitor/${COMPAT_NAME}
        install -d ${D}${datadir}/phosphor-fan-presence/presence/${COMPAT_NAME}

        for CONTROL_CONFIG in ${CONTROL_CONFIGS}
        do
                install -m 0644 ${UNPACKDIR}/${CONTROL_CONFIG} \
                        ${D}${datadir}/phosphor-fan-presence/control/${COMPAT_NAME}
        done

        install -m 0644 ${UNPACKDIR}/groups_2p.json \
                ${D}${datadir}/phosphor-fan-presence/control/${COMPAT_NAME}/groups.json
        install -m 0644 ${UNPACKDIR}/monitor.json \
                ${D}${datadir}/phosphor-fan-presence/monitor/${COMPAT_NAME}/config.json
        install -m 0644 ${UNPACKDIR}/presence.json \
                ${D}${datadir}/phosphor-fan-presence/presence/${COMPAT_NAME}/config.json
}

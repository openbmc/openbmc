FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " json"

CONFIG_FOLDER = "phosphor-fan-presence"
COMPAT_NAME = "com.ampere.Hardware.Chassis.Model.MtJefferson"
CONTROL_CONFIGS = "events.json fans.json zones.json groups.json"
EXECCON_OVERRIDE_CONF = "phosphor-fan-override-execcon.conf"

SRC_URI:append = " \
                  file://events.json \
                  file://fans.json \
                  file://groups.json \
                  file://zones.json \
                  file://monitor.json \
                  file://presence.json \
                  file://${EXECCON_OVERRIDE_CONF} \
                 "

SYSTEMD_OVERRIDE:${PN}-control += "${EXECCON_OVERRIDE_CONF}:${TMPL_CONTROL}.d/${EXECCON_OVERRIDE_CONF}"
SYSTEMD_OVERRIDE:${PN}-monitor += "${EXECCON_OVERRIDE_CONF}:${TMPL_MONITOR}.d/${EXECCON_OVERRIDE_CONF}"
SYSTEMD_OVERRIDE:${PN}-presence-tach += "${EXECCON_OVERRIDE_CONF}:${TMPL_TACH}.d/${EXECCON_OVERRIDE_CONF}"

do_install:append () {
    # datadir = /usr/share
    install -d ${D}${datadir}/${CONFIG_FOLDER}/control/${COMPAT_NAME}
    install -d ${D}${datadir}/${CONFIG_FOLDER}/monitor/${COMPAT_NAME}
    install -d ${D}${datadir}/${CONFIG_FOLDER}/presence/${COMPAT_NAME}

    for CONTROL_CONFIG in ${CONTROL_CONFIGS}
    do
        install -m 0644 ${UNPACKDIR}/${CONTROL_CONFIG} \
            ${D}${datadir}/${CONFIG_FOLDER}/control/${COMPAT_NAME}
    done

    install -m 0644 ${UNPACKDIR}/monitor.json \
        ${D}${datadir}/${CONFIG_FOLDER}/monitor/${COMPAT_NAME}/config.json
    install -m 0644 ${UNPACKDIR}/presence.json \
        ${D}${datadir}/${CONFIG_FOLDER}/presence/${COMPAT_NAME}/config.json
}

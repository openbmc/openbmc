FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS = " \
    exitairtempsensor \
    external \
    intelcpusensor \
    intrusionsensor \
    ipmbsensor \
    mcutempsensor \
"
PACKAGECONFIG:remove = " \
    ${FACEBOOK_REMOVED_DBUS_SENSORS} \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS = " \
    exitairtempsensor \
    external \
    intelcpusensor \
    intrusionsensor \
    ipmbsensor \
    mcutempsensor \
    ${@bb.utils.contains('MACHINE_FEATURES', 'fb-fanless', 'fansensor', '',d)} \
"
PACKAGECONFIG:remove = " \
    ${FACEBOOK_REMOVED_DBUS_SENSORS} \
"

PACKAGECONFIG:append:mf-fb-liquid-cooled = " \
    leakdetector \
"

PACKAGECONFIG:append:fb-compute-nvidia = " \
    smbpbi \
"

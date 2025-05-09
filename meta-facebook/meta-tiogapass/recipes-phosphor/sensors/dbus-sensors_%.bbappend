FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove= " \
        intelcpusensor \
        ipmbsensor \
        "

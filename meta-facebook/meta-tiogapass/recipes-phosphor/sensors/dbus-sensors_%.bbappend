FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove= " \
        intelcpusensor \
        ipmbsensor \
        "

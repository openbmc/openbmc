FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:append = " \
    nvmesensor \
"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGECONFIG:append = " \
    nvmesensor \
"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

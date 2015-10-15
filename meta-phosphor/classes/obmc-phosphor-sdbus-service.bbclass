# Common code for applications providing a D-Bus service using sd-bus bindings.

# Class users should define DBUS_SERVICES prior to including.

DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"

inherit obmc-phosphor-dbus-service

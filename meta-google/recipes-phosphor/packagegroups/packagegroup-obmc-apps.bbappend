# Remove unneeded packages installed
RDEPENDS:${PN}-extras:remove:gbmc = "obmc-ikvm"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-rest"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-dbus-monitor"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-nslcd-cert-config"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-nslcd-authority-cert-config"

RDEPENDS:${PN}-extrasdev:remove:gbmc = "rest-dbus"

# Disable bmcweb for gbmc machines without redfish.
RDEPENDS:${PN}-extras:remove:gbmc = '${@bb.utils.contains:any("MACHINE_FEATURES", ['redfish'], "", "bmcweb phosphor-bmcweb-cert-config", d)}'

# Disable remote logging.
# FEATURE_PACKAGES:obmc-remote-logging-mgmt ?= "packagegroup-obmc-apps-remote-logging"
RDEPENDS:${PN}-remote-logging:remove:gbmc = "rsyslog"
RDEPENDS:${PN}-remote-logging:remove:gbmc = "rsyslog-policy"
RDEPENDS:${PN}-remote-logging:remove:gbmc = "phosphor-rsyslog-config"

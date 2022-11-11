# Remove unneeded packages installed
RDEPENDS:${PN}-extras:remove:gbmc = "obmc-ikvm"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-nslcd-cert-config"
RDEPENDS:${PN}-extras:remove:gbmc = "phosphor-nslcd-authority-cert-config"
RDEPENDS:${PN}-devtools:remove:gbmc = "trace-enable"

# Disable bmcweb for gbmc machines without redfish.
RDEPENDS:${PN}-extras:remove:gbmc = '${@bb.utils.contains:any("MACHINE_FEATURES", ['redfish'], "", "bmcweb", d)}'

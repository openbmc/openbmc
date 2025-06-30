FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = " \
     -Dhttp-body-limit=65 \
"

PACKAGECONFIG:append = " \
     redfish-bmc-journal \
"

PACKAGECONFIG:remove = " \
     redfish-allow-deprecated-power-thermal \
"

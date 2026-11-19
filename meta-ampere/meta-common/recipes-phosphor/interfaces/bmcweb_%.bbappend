FILESEXTRAPATHS:append := "${THISDIR}/${BPN}:"

EXTRA_OEMESON:append = " \
     -Dhttp-body-limit=65 \
"

PACKAGECONFIG:append = " \
     redfish-bmc-journal \
"

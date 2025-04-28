FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

PACKAGECONFIG:append:qcom-bmc-ast2600 = " \
    redfish-bmc-journal \
    redfish-dbus-log \
"

EXTRA_OEMESON:append:qcom-bmc-ast2600 = " \
    -Dhttp-body-limit=128 \
"

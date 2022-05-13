RMCPP_IFACE = "eth1"

FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
    file://0006-Modify-dbus-namespace-of-chassis-control-for-guid.patch \
    "

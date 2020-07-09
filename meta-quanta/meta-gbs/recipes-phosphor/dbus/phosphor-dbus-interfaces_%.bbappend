FILESEXTRAPATHS_prepend_gbs := "${THISDIR}/${PN}:"

SRC_URI_append_gbs = " file://0024-Add-the-pre-timeout-interrupt-defined-in-IPMI-spec.patch \
                       file://0025-Add-PreInterruptFlag-properity-in-DBUS.patch \
                     "

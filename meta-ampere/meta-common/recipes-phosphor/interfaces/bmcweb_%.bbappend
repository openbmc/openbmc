FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

EXTRA_OEMESON_append = " \
    -Dredfish-bmc-journal=enabled \
    -Dbmcweb-logging=enabled \
    "

SRC_URI += "file://0001-Redfish-Add-message-registries-for-Ampere-event.patch"

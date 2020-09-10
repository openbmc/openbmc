FILESEXTRAPATHS_append_mtjade := "${THISDIR}/${PN}:"

EXTRA_OECMAKE_append = " \
    -DBMCWEB_ENABLE_LOGGING_MW=ON \
    -DBMCWEB_ENABLE_REDFISH_BMC_JOURNAL=ON \
    "

SRC_URI += "file://0001-Redfish-Add-message-registries-for-Ampere-event.patch"

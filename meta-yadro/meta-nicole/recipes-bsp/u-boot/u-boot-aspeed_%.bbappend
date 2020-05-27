FILESEXTRAPATHS_append := "${THISDIR}/files:"

SRC_URI_append = " \
    file://0001-Add-system-reset-status-support.patch \
    file://0002-config-ast-common-set-fieldmode-to-true.patch \
    "

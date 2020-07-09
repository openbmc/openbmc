FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append  = "\
    file://0001-Add-support-for-persistent-only-settings.patch \
    file://0002-Add-support-for-boot-initiator-mailbox.patch \
"

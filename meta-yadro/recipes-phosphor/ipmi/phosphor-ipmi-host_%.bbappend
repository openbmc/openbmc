FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
    file://0001-Add-support-for-persistent-only-settings.patch \
    file://0002-Add-support-for-boot-initiator-mailbox.patch \
    file://0003-Fix-version-parsing-update-AUX-revision-info.patch \
"

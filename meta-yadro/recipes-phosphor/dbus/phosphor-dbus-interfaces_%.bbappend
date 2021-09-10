FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = "\
    file://0001-Add-boot-initiator-mailbox-interface.patch \
"

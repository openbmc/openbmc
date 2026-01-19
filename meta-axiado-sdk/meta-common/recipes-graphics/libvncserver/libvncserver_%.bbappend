FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# KWS-4884
SRC_URI += "file://0001-RGB-color-swapping-for-axiado-kvm.patch"

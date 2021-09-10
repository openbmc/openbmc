FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:yosemitev2 = " file://settings-manager.override.yml"

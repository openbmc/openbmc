FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:yosemitev2 = " file://gpio_defs.json"
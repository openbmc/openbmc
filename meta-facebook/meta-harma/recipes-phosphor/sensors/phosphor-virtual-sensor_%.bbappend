FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:harma = " file://virtual_sensor_config.json "

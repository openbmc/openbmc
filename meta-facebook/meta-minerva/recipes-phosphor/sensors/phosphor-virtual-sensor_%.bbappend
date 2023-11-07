FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:minerva = " file://virtual_sensor_config.json "

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ventura = " file://virtual_sensor_config.json "

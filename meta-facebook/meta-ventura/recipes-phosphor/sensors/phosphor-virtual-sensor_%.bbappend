FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:ventura = " file://virtual_sensor_config.json "

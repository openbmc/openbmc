FILESEXTRAPATHS_append_witherspoon := ":${THISDIR}/${PN}"
SRC_URI_append_witherspoon = " file://occ_sensors.hardcoded.yaml \
                               file://dev_id.json \
                               file://dcmi_temp_readings.json \
                             "

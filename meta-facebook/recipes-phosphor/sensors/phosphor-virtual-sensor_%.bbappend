SENSOR_CONFIGS ?= "virtual_sensor_config.json"

SRC_URI:append = "\
    ${@ ' '.join([ 'file://' + x for x in d.getVar('SENSOR_CONFIGS').split()])} \
    "

do_install:append() {
    # Delete the default one from Meson.
    if [ -e "${D}${datadir}/phosphor-virtual-sensor/virtual_sensor_config.json" ]; then
        rm "${D}${datadir}/phosphor-virtual-sensor/virtual_sensor_config.json"
    fi

    # Install the ones from our meta-layer.
    install -d ${D}${datadir}/phosphor-virtual-sensor
    for s in ${SENSOR_CONFIGS}; do
        install -m 0644 ${UNPACKDIR}/$s ${D}${datadir}/phosphor-virtual-sensor
    done
}

SUMMARY = "Sensor config for phosphor-host-ipmi"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create an output yaml file with
        # sensor data extracted  from the mrw.
        # provides a sample output file.

        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install config.yaml ${DEST}/sensor.yaml
}

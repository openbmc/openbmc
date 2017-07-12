SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           packagegroup-obmc-ipmi-sensors \
           "

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_ipmi_sensor.pl \
            -i ${datadir}/obmc-mrw/${MACHINE}.xml \
            -m ${sensor_yamldir} \
            -o ${DEST}/sensor.yaml
}

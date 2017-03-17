SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_ipmi_sensor.pl \
            -i ${datadir}/obmc-mrw/${MACHINE}.xml \
            -m config.yaml \
            -o ${DEST}/sensor.yaml
}

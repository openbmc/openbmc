SUMMARY = "YAML configuration for ACx22 systems"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit mrw-xml

SRC_URI:ibm-ac-server = " \
    file://acx22-ipmi-fru-bmc.yaml \
    file://acx22-ipmi-fru-not-sent-by-host.yaml \
    file://acx22-ipmi-hwmon-sensors.yaml \
    file://acx22-ipmi-inventory-sensors.yaml \
    file://acx22-ipmi-occ-sensors.yaml \
    file://acx22-ipmi-sensors-mrw.yaml \
    "
DEPENDS = " \
    mrw-native \
    mrw-perl-tools-native \
    openpower-yaml-config \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

ACx22_IPMI_EXTRA_FRU_READ_YAMLS:ibm-ac-server = " \
    acx22-ipmi-fru-bmc.yaml \
    acx22-ipmi-fru-not-sent-by-host.yaml \
    "
ACx22_IPMI_EXTRA_SENSOR_YAMLS = " \
    acx22-ipmi-hwmon-sensors.yaml \
    acx22-ipmi-occ-sensors.yaml \
    "

do_install() {
    perlbin="${STAGING_DIR_NATIVE}${bindir}/perl-native/perl"
    scriptpath=${STAGING_DIR_NATIVE}${bindir}
    mrw=${STAGING_DIR_NATIVE}${datadir}/obmc-mrw/${MRW_XML}
    op_configpath=${STAGING_DIR_HOST}${datadir}/openpower-yaml-config

    # generate extra-properties.yaml from the MRW for ipmi-fru-parser
    $perlbin $scriptpath/gen_fru_properties.pl -m $mrw \
        -c $op_configpath/ipmi-fru-properties-mrw.yaml \
        -o extra-properties.yaml ${EXTRA_MRW_SCRIPT_ARGS}

    # generate fru-read.yaml from the MRW, for ipmid and ipmi-fru-parser
    $perlbin $scriptpath/gen_ipmi_fru.pl -i $mrw \
        -m $op_configpath/ipmi-hostboot-fru-mrw.yaml \
        -o fru-read-partial.yaml ${EXTRA_MRW_SCRIPT_ARGS}
    cat fru-read-partial.yaml ${ACx22_IPMI_EXTRA_FRU_READ_YAMLS} \
        > fru-read.yaml

    # generate inventory-sensors.yaml from the MRW, for ipmid
    $perlbin $scriptpath/gen_ipmi_sel.pl -i $mrw \
        -m acx22-ipmi-inventory-sensors.yaml -o inventory-sensors.yaml \
        ${EXTRA_MRW_SCRIPT_ARGS}

    # generate sensors.yaml from the MRW, for ipmid
    cat acx22-ipmi-sensors-mrw.yaml \
        $op_configpath/ipmi-hostboot-volatile-sensor-mrw.yaml \
        $op_configpath/ipmi-occ-active-sensor-mrw.yaml \
        > sensors-mrw.yaml
    $perlbin $scriptpath/gen_ipmi_sensor.pl -i $mrw -m sensors-mrw.yaml \
        -o sensors-partial.yaml ${EXTRA_MRW_SCRIPT_ARGS}
    cat sensors-partial.yaml ${ACx22_IPMI_EXTRA_SENSOR_YAMLS} \
        > sensors.yaml

    install -m 0644 -D extra-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D fru-read.yaml ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D inventory-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
    install -m 0644 -D sensors.yaml ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    "

ALLOW_EMPTY:${PN} = "1"

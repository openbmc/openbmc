SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host
inherit pythonnative
inherit mrw-xml

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           phosphor-ipmi-sensor-config-native \
           phosphor-ipmi-sensor-inventory-mrw-config-native \
           python-pyyaml-native \
           "

PROVIDES += "virtual/phosphor-ipmi-sensor-inventory"

S = "${WORKDIR}"
SRC_URI += "file://merge_sensor_config.py"

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_ipmi_sensor.pl \
            -i ${mrw_datadir}/${MRW_XML} \
            -m ${sensor_yamldir}/config.yaml \
            -o ${DEST}/sensor.yaml
}

python do_merge_sensor_config () {
    import subprocess

    # TODO: Perform the merge in a temporary directory?
    workdir = d.getVar('WORKDIR', True)
    nativedir = d.getVar('STAGING_DIR_NATIVE', True)
    sensoryamldir = d.getVar('sensor_yamldir', True)
    cmd = []
    cmd.append(os.path.join(workdir, 'merge_sensor_config.py'))
    cmd.append(os.path.join(sensoryamldir, 'config.yaml'))

    fetch = os.listdir(sensoryamldir)
    override_urls = [url for url in fetch if url.endswith('-config.yaml')]
    for url in override_urls:
        bb.debug(2, 'Merging extra configurations: ' + url)
        filename = os.path.join(sensoryamldir, url)
        cmd.append(filename)

    # Invoke the script and don't catch any resulting exception.
    subprocess.check_call(cmd)
}
# python-pyyaml-native is installed by do_configure, so put this task after
addtask merge_sensor_config after do_configure before do_compile

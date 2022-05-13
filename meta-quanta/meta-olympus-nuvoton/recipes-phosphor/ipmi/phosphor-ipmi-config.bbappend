FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit image_version

SRC_URI:append:olympus-nuvoton = " file://channel_config.json"
SRC_URI:append:olympus-nuvoton = " file://dev_id.json"
SRC_URI:append:olympus-nuvoton = " file://power_reading.json"
SRC_URI:append:olympus-nuvoton = " file://dcmi_sensors.json"

do_install:append:olympus-nuvoton() {
    install -m 0644 -D ${WORKDIR}/channel_config.json \
        ${D}${datadir}/ipmi-providers/channel_config.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
    install -m 0644 -D ${WORKDIR}/power_reading.json \
        ${D}${datadir}/ipmi-providers/power_reading.json
    install -m 0644 -D ${WORKDIR}/dcmi_sensors.json \
        ${D}${datadir}/ipmi-providers/dcmi_sensors.json
}

unset do_patch[noexec]
do_patch[depends] = "os-release:do_populate_sysroot"

python do_patch() {
    import json
    import re
    from shutil import copyfile
    version_id = do_get_version(d)

    # count from the commit version
    count = re.findall("-(\d{1,4})-", version_id)

    release = re.findall("-r(\d{1,4})", version_id)
    if release:
        auxVer = count[0] + "{0:0>4}".format(release[0])
    else:
        auxVer = count[0] + "0000"

    workdir = d.getVar('WORKDIR', True)
    file = os.path.join(workdir, 'dev_id.json')

    # Update dev_id.json with the auxiliary firmware revision
    with open(file, "r+") as jsonFile:
        data = json.load(jsonFile)
        jsonFile.seek(0)
        jsonFile.truncate()
        data["aux"] = int(auxVer, 16)
        json.dump(data, jsonFile)
}

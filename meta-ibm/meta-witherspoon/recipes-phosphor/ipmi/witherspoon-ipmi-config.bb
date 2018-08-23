SUMMARY = "Witherspoon IPMI daemon configuration"
PR = "r1"

inherit obmc-phosphor-license
inherit allarch

SRC_URI = " \
    file://cipher_list.json \
    file://dcmi_cap.json \
    file://dcmi_sensors.json \
    file://dev_id.json \
    file://power_reading.json \
    "

FILES_${PN} = " \
    ${datadir}/ipmi-providers/cipher_list.json \
    ${datadir}/ipmi-providers/dcmi_cap.json \
    ${datadir}/ipmi-providers/dcmi_sensors.json \
    ${datadir}/ipmi-providers/dev_id.json \
    ${datadir}/ipmi-providers/power_reading.json \
    "

do_fetch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Calculate the auxiliary firmware revision to be updated in the dev_id.json
# file. It is calculated from the VERSION_ID field which currently has two
# formats. The revision field is 4 bytes, the first two bytes represent the
# count of commits from the tagging and next two bytes represent the version.
# Both fields are represented in BCD encoded format, so 9999 is the maximum
# value both fields can take. With the format "v2.1-216-ga78ace8", Petitboot
# would display the firmware revision as "Firmware version: 2.01.02160000",
# "0216" is count and the revision is "0000". With the format
# "ibm-v2.0-10-r41-0-gd0c319e" Petitboot would display the firmware revision
# as "Firmware version: 2.00.00100041", "0010" is count and the revision
# is "0041".

inherit image_version

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

do_install() {
    install -d ${D}${datadir}/ipmi-providers
    install -m 0644 -D ${WORKDIR}/cipher_list.json \
        ${D}${datadir}/ipmi-providers/cipher_list.json
    install -m 0644 -D ${WORKDIR}/dcmi_cap.json \
        ${D}${datadir}/ipmi-providers/dcmi_cap.json
    install -m 0644 -D ${WORKDIR}/dcmi_sensors.json \
        ${D}${datadir}/ipmi-providers/dcmi_sensors.json
    install -m 0644 -D ${WORKDIR}/dev_id.json \
        ${D}${datadir}/ipmi-providers/dev_id.json
    install -m 0644 -D ${WORKDIR}/power_reading.json \
        ${D}${datadir}/ipmi-providers/power_reading.json
}

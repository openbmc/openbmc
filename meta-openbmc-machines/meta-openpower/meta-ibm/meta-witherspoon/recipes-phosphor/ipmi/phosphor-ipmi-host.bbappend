FILESEXTRAPATHS_append_witherspoon := ":${THISDIR}/${PN}"
SRC_URI_append_witherspoon = " file://occ_sensors.hardcoded.yaml \
                               file://dev_id.json \
                               file://dcmi_sensors.json \
                               file://power_reading.json \
                               file://hwmon_sensors.hardcoded.yaml \
                               file://cipher_list.json \
                             "
inherit image_version

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

python do_populate_aux_version() {
        import json
        import re
        version_id = do_get_version(d)

        # count from the commit version
        count = re.findall("-(\d{1,4})-", version_id)

        release = re.findall("-r(\d{1,4})", version_id)
        if release:
            auxVer = count[0] + "{0:0>4}".format(release[0])
        else:
            auxVer = count[0] + "0000"

        # Update dev_id.json with the auxiliary firmware revision
        workdir = d.getVar('WORKDIR', True)
        file = os.path.join(workdir, 'dev_id.json')

        with open(file, "r+") as jsonFile:
            data = json.load(jsonFile)
            jsonFile.seek(0)
            jsonFile.truncate()
            data["aux"] = int(auxVer, 16)
            json.dump(data, jsonFile)
}

addtask populate_aux_version after do_configure before do_compile

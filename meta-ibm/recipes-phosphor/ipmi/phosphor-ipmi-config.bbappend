FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

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

unset do_patch[noexec]
do_patch[depends] = "os-release:do_populate_sysroot"

python do_patch:ibm-ac-server() {
    import json
    import re
    from shutil import copyfile
    version_id = do_get_version(d)

    # count from the commit version, minimum of one digit
    count = re.findall("-(\d{1,4})-", version_id)
    if count:
        commit = count[0]
    else:
        commit = "0"

    release = re.findall("-r(\d{1,4})", version_id)
    if release:
        auxVer = commit + "{0:0>4}".format(release[0])
    else:
        auxVer = commit + "0000"

    unpackdir = d.getVar('UNPACKDIR', True)
    file = os.path.join(unpackdir, 'dev_id.json')

    # Update dev_id.json with the auxiliary firmware revision
    with open(file, "r+") as jsonFile:
        data = json.load(jsonFile)
        jsonFile.seek(0)
        jsonFile.truncate()
        data["aux"] = int(auxVer, 16)
        json.dump(data, jsonFile)
}

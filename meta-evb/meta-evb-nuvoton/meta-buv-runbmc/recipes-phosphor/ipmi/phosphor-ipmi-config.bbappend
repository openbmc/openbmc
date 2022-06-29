FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

inherit image_version

unset do_patch[noexec]
do_patch[depends] = "os-release:do_populate_sysroot"

python do_patch:buv-runbmc() {
    import json
    import re
    from shutil import copyfile
    version_id = do_get_version(d)

    # count from the commit version
    count = re.findall("-(\d{1,4})-", version_id)
    if len(count) == 0:
        count.append("0")

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

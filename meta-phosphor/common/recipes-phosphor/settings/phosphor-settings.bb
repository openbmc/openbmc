SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit setuptools
inherit obmc-phosphor-dbus-service
inherit pythonnative

include phosphor-settings-manager.inc

DBUS_SERVICE_${PN} = "org.openbmc.settings.Host.service"

DEPENDS += "python-pyyaml-native"
RDEPENDS_${PN} += "python-dbus python-pygobject python-ipy"
PROVIDES += "virtual/obmc-settings-mgmt"
RPROVIDES_${PN} += "virtual-obmc-settings-mgmt"

SRC_URI += "file://merge_settings.py"

S = "${WORKDIR}/git"

# Collect files in SRC_URI that end in ".override.yml" and call a script that
# writes their contents over that of settings.yaml, which is then updated to
# the merged data values.
# This doesn't correctly handle globs in ".override.yml" entries in SRC_URI.
python do_merge_settings () {
    import subprocess

    # TODO: Perform the merge in a temporary directory?
    workdir = d.getVar('WORKDIR', True)
    cmd = []
    cmd.append(os.path.join(workdir, 'merge_settings.py'))
    cmd.append(os.path.join(d.getVar('S', True), 'settings.yaml'))

    fetch = bb.fetch2.Fetch([], d)
    override_urls = filter(lambda f: f.endswith('.override.yml'), fetch.urls)
    for url in override_urls:
        bb.debug(2, 'Overriding with source: ' + url)
        local_base = os.path.basename(fetch.localpath(url))
        filename = os.path.join(workdir, local_base)
        cmd.append(filename)

    # Invoke the script and don't catch any resulting exception.
    subprocess.check_call(cmd)
}
# python-pyyaml-native is installed by do_configure, so put this task after
addtask merge_settings after do_configure before do_compile

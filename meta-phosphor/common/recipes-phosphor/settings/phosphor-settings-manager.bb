SUMMARY = "Phosphor Settings Manager"
DESCRIPTION = "Phosphor Settings Manager is an application that creates \
d-bus objects to represent various user settings."
PR = "r1"

inherit autotools
inherit obmc-phosphor-dbus-service
inherit pythonnative
inherit phosphor-settings-manager

require phosphor-settings-manager.inc

DBUS_SERVICE_${PN} = "xyz.openbmc_project.Settings.service"

DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "virtual/phosphor-settings-defaults"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"
DEPENDS += "cereal"

RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

S = "${WORKDIR}/git"
SRC_URI += "file://merge_settings.py"

EXTRA_OECONF = " \
             SETTINGS_YAML=${STAGING_DIR_NATIVE}${settings_datadir}/defaults.yaml \
             "

# Collect files in SRC_URI that end in ".override.yml" and call a script that
# writes their contents over that of settings.yaml, which is then updated to
# the merged data values.
# This doesn't correctly handle globs in ".override.yml" entries in SRC_URI.
python do_merge_settings () {
    import subprocess

    # TODO: Perform the merge in a temporary directory?
    workdir = d.getVar('WORKDIR', True)
    nativedir = d.getVar('STAGING_DIR_NATIVE', True)
    settingsdir = d.getVar('settings_datadir', True)
    settingsdir = settingsdir[1:]
    settingsdir = os.path.join(nativedir, settingsdir)
    cmd = []
    cmd.append(os.path.join(workdir, 'merge_settings.py'))
    cmd.append(os.path.join(settingsdir, 'defaults.yaml'))

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

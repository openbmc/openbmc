SUMMARY = "Phosphor Settings Manager"
DESCRIPTION = "Phosphor Settings Manager is an application that creates \
d-bus objects to represent various user settings."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit obmc-phosphor-dbus-service
inherit python3native
inherit phosphor-settings-manager

require phosphor-settings-manager.inc

DBUS_SERVICE_${PN} = "xyz.openbmc_project.Settings.service"

DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "virtual/phosphor-settings-defaults"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'obmc-mrw', 'phosphor-settings-read-settings-mrw-native', '', d)}"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "libcereal"

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
    # Used for any settings from the MRW
    use_mrw = bb.utils.contains('DISTRO_FEATURES', 'obmc-mrw', 'true', '', d)
    if (use_mrw == 'true'):
        cmd.append(os.path.join(settingsdir, 'mrw-settings.override.yaml'))

    fetch = bb.fetch2.Fetch([], d)
    override_urls = [url for url in fetch.urls if url.endswith('.override.yml')]
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

SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit setuptools
inherit obmc-phosphor-dbus-service
inherit pythonnative

DBUS_SERVICE_${PN} = "org.openbmc.settings.Host.service"

DEPENDS += "python-pyyaml-native"
RDEPENDS_${PN} += "python-dbus python-pygobject"
PROVIDES += "virtual/obmc-settings-mgmt"
RPROVIDES_${PN} += "virtual-obmc-settings-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "aaa74c14cd5799ac560dc92445a14c28832026c1"

S = "${WORKDIR}/git"

python do_path_test () {
    sys.path.append(os.path.join(d.getVar('STAGING_DIR_NATIVE', True),
            d.getVar('PYTHON_SITEPACKAGES_DIR', True)[1:]))
    import yaml
}
addtask path_test after do_configure before do_compile

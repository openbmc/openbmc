SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "5a1fc9aa4e1e6d6709fac3c335fd3de9ccbcbee4"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/settings_file.py ${D}/${sbindir}
        install ${S}/settings_manager.py ${D}/${sbindir}
}


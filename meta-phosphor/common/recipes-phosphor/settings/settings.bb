SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "df9b8ad2d53df7893ff0a49708ef05c3289653d4"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/settings_file.py ${D}/${sbindir}
        install ${S}/settings_manager.py ${D}/${sbindir}
}


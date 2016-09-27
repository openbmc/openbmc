SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "5b090c61086f196fbaa9279ae7728d3680f52e43"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/settings_file.py ${D}/${sbindir}
        install ${S}/settings_manager.py ${D}/${sbindir}
}


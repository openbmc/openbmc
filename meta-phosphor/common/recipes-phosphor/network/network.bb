SUMMARY = "Network DBUS object"
DESCRIPTION = "Network DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-networkd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git://github.com/openbmc/phosphor-networkd"

SRCREV = "682512485947977a8d130a1cf9832b12583ea253"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/${sbindir}
        install ${S}/netman.py ${D}/${sbindir}
}


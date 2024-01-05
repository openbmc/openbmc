SUMMARY = "Supervisor: A Process Control System"
DESCRIPTION = "\
Supervisor is a client/server system that allows its users \
to monitorand control a number of processes on UNIX-like \
operating systems."
HOMEPAGE = "https://github.com/Supervisor/supervisor"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=5b4e3a2172bba4c47cded5885e7e507e"

SRC_URI[sha256sum] = "34761bae1a23c58192281a5115fb07fbf22c9b0133c08166beffc70fed3ebc12"

PYPI_PACKAGE = "supervisor"
inherit pypi systemd setuptools3
RDEPENDS:${PN} = "\
    python3-meld3 \
    python3-io \
    python3-xmlrpc \
    python3-resource \
    python3-setuptools \
"

SRC_URI += "file://supervisord.conf \
	    file://supervisor.service \
	"
SYSTEMD_SERVICE:${PN} = "supervisor.service"

do_install:append() {
	install -d ${D}${sysconfdir}/supervisor
	install -d ${D}${systemd_system_unitdir}

	install -m 0644 ${WORKDIR}/supervisord.conf ${D}${sysconfdir}/supervisor
	install -m 0644 ${WORKDIR}/supervisor.service ${D}${systemd_system_unitdir}
}

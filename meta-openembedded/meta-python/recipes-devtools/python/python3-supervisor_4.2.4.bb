SUMMARY = "Supervisor: A Process Control System"
DESCRIPTION = "\
Supervisor is a client/server system that allows its users \
to monitorand control a number of processes on UNIX-like \
operating systems."
HOMEPAGE = "https://github.com/Supervisor/supervisor"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=5b4e3a2172bba4c47cded5885e7e507e"

SRC_URI[sha256sum] = "40dc582ce1eec631c3df79420b187a6da276bbd68a4ec0a8f1f123ea616b97a2"

PYPI_PACKAGE = "supervisor"
inherit pypi systemd setuptools3
RDEPENDS:${PN} = "\
    ${PYTHON_PN}-meld3 \
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

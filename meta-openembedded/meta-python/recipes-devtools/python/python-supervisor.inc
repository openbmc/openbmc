SUMMARY = "Supervisor: A Process Control System"
DESCRIPTION = "\
Supervisor is a client/server system that allows its users \
to monitorand control a number of processes on UNIX-like \
operating systems."
HOMEPAGE = "https://github.com/Supervisor/supervisor"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=5b4e3a2172bba4c47cded5885e7e507e"

SRC_URI[md5sum] = "ac70dadd051dcaf1d03c467d51a3aac1"
SRC_URI[sha256sum] = "5604457da4fb89d850e78c128205fe4938c9277f87301885b33c94c60361e575"

PYPI_PACKAGE = "supervisor"
inherit pypi systemd
RDEPENDS_${PN} = "\
    ${PYTHON_PN}-meld3 \
"

SRC_URI += "file://supervisord.conf \
	    file://supervisor.service \
	"
SYSTEMD_SERVICE_${PN} = "supervisor.service"

do_install_append() {
	install -d ${D}${sysconfdir}/supervisor
	install -d ${D}${systemd_system_unitdir}

	install -m 0644 ${WORKDIR}/supervisord.conf ${D}${sysconfdir}/supervisor
	install -m 0644 ${WORKDIR}/supervisor.service ${D}${systemd_system_unitdir}
}

SUMMARY = "Very simple session manager for X"
HOMEPAGE = "http://www.yoctoproject.org"
BUGTRACKER = "http://bugzilla.pokylinux.org"

PR = "r4"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://mini-x-session;endline=5;md5=b6430bffbcf05f9760e72938826b7487"

SECTION = "x11"
RCONFLICTS_${PN} = "matchbox-common"

SRC_URI = "file://mini-x-session"
S = "${WORKDIR}"

RDEPENDS_${PN} = "sudo"

inherit update-alternatives

ALTERNATIVE_${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/mini-x-session"
ALTERNATIVE_PRIORITY = "50"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/mini-x-session ${D}/${bindir}
}

SUMMARY = "Very simple session manager for X"
DESCRIPTION = "Simple session manager for X, that provides just the right boilerplate to create a session and launch the browser "
HOMEPAGE = "http://www.yoctoproject.org"
BUGTRACKER = "http://bugzilla.pokylinux.org"

PR = "r4"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://mini-x-session;endline=5;md5=b6430bffbcf05f9760e72938826b7487"

SECTION = "x11"
RCONFLICTS:${PN} = "matchbox-common"

SRC_URI = "file://mini-x-session"
S = "${WORKDIR}"

RDEPENDS:${PN} = "sudo"

inherit update-alternatives

ALTERNATIVE:${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/mini-x-session"
ALTERNATIVE_PRIORITY = "50"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/mini-x-session ${D}/${bindir}
}

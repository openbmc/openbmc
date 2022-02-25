SUMMARY = "Custom Matchbox session files"
DESCRIPTION = "Very simple session manager for matchbox tools"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://matchbox-session;endline=20;md5=180f1c169a15d059a56c30094f6fb5ea"

SECTION = "x11"
RCONFLICTS:${PN} = "matchbox-common"

SRC_URI = "file://matchbox-session"
S = "${WORKDIR}"

PR = "r4"

inherit update-alternatives

ALTERNATIVE:${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/matchbox-session"
ALTERNATIVE_PRIORITY = "100"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/matchbox-session ${D}/${bindir}
}

SUMMARY = "Custom Matchbox session files"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://matchbox-session;endline=20;md5=180f1c169a15d059a56c30094f6fb5ea"

SECTION = "x11"
RCONFLICTS_${PN} = "matchbox-common"

SRC_URI = "file://matchbox-session"
S = "${WORKDIR}"

PR = "r4"

inherit update-alternatives

ALTERNATIVE_${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/matchbox-session"
ALTERNATIVE_PRIORITY = "100"

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/matchbox-session ${D}/${bindir}
}

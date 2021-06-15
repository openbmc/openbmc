SUMMARY = "Programmable Completion for Bash 4"
DESCRIPTION = "Collection of command line command completions for the Bash shell, \
collection of helper functions to assist in creating new completions, \
and set of facilities for loading completions automatically on demand, as well \
as installing them."

HOMEPAGE = "https://github.com/scop/bash-completion"
BUGTRACKER = "https://github.com/scop/bash-completion/issues"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "console/utils"

SRC_URI = "https://github.com/scop/bash-completion/releases/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "2514c6772d0de6254758b98c53f91861"
SRC_URI[sha256sum] = "73a8894bad94dee83ab468fa09f628daffd567e8bef1a24277f1e9a0daf911ac"
UPSTREAM_CHECK_REGEX = "bash-completion-(?P<pver>(?!2008).+)\.tar"
UPSTREAM_CHECK_URI = "https://github.com/scop/bash-completion/releases"

PARALLEL_MAKE = ""

inherit autotools

do_install_append() {
	# compatdir
	install -d ${D}${sysconfdir}/bash_completion.d/
	echo '. ${datadir}/${BPN}/bash_completion' >${D}${sysconfdir}/bash_completion

}

RDEPENDS_${PN} = "bash"

# Some recipes are providing ${PN}-bash-completion packages
PACKAGES =+ "${PN}-extra"
FILES_${PN}-extra = "${datadir}/${BPN}/completions/ \
    ${datadir}/${BPN}/helpers/"

FILES_${PN}-dev += "${datadir}/cmake"

BBCLASSEXTEND = "nativesdk"

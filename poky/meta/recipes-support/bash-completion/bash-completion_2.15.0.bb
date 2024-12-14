SUMMARY = "Programmable Completion for Bash 4"
DESCRIPTION = "Collection of command line command completions for the Bash shell, \
collection of helper functions to assist in creating new completions, \
and set of facilities for loading completions automatically on demand, as well \
as installing them."

HOMEPAGE = "https://github.com/scop/bash-completion"
BUGTRACKER = "https://github.com/scop/bash-completion/issues"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "console/utils"

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[sha256sum] = "976a62ee6226970283cda85ecb9c7a4a88f62574c0a6f9e856126976decf1a06"
GITHUB_BASE_URI = "https://github.com/scop/bash-completion/releases"

PARALLEL_MAKE = ""

inherit autotools github-releases

do_install:append() {
	# compatdir
	install -d ${D}${sysconfdir}/bash_completion.d/
	echo '. ${datadir}/${BPN}/bash_completion' >${D}${sysconfdir}/bash_completion

}

RDEPENDS:${PN} = "bash"

# Some recipes are providing ${PN}-bash-completion packages
PACKAGES =+ "${PN}-extra"
FILES:${PN}-extra = "${datadir}/${BPN}/completions/ \
    ${datadir}/${BPN}/helpers/"

BBCLASSEXTEND = "nativesdk"

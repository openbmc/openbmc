SUMMARY = "Programmable Completion for Bash 4"
DESCRIPTION = "bash completion extends bash's standard completion behavior to \
achieve complex command lines with just a few keystrokes."
HOMEPAGE = "https://github.com/scop/bash-completion"
BUGTRACKER = "https://github.com/scop/bash-completion/issues"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "console/utils"

SRC_URI = "https://github.com/scop/bash-completion/releases/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "f376ae3266cc70017aa833c39b76f984"
SRC_URI[sha256sum] = "123c17998e34b937ce57bb1b111cd817bc369309e9a8047c0bcf06ead4a3ec92"
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

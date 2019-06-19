SUMMARY = "Programmable Completion for Bash 4"
HOMEPAGE = "http://bash-completion.alioth.debian.org/"
BUGTRACKER = "https://alioth.debian.org/projects/bash-completion/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "console/utils"

SRC_URI = "https://github.com/scop/bash-completion/releases/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "60139333b25c4d54ad40323b701be130"
SRC_URI[sha256sum] = "d48fe378e731062f479c5f8802ffa9d3c40a275a19e6e0f6f6cc4b90fa12b2f5"
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

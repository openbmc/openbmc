SUMMARY = "Programmable Completion for Bash 4"
HOMEPAGE = "http://bash-completion.alioth.debian.org/"
BUGTRACKER = "https://alioth.debian.org/projects/bash-completion/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "console/utils"

SRC_URI = "https://github.com/scop/bash-completion/releases/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "201b6ae62f7d5fb5b1b25e34427db919"
SRC_URI[sha256sum] = "c01f5570f5698a0dda8dc9cfb2a83744daa1ec54758373a6e349bd903375f54d"
UPSTREAM_CHECK_REGEX = "bash-completion-(?P<pver>(?!2008).+)\.tar"
UPSTREAM_CHECK_URI = "https://github.com/scop/bash-completion/releases"

PARALLEL_MAKE = ""

inherit autotools

do_install_append() {
	# compatdir
	install -d ${D}${sysconfdir}/bash_completion.d/
	echo '. ${datadir}/${BPN}/bash_completion' >${D}${sysconfdir}/bash_completion

	# Delete files already provided by util-linux
	local i
	for i in mount umount; do
		rm ${D}${datadir}/${BPN}/completions/$i
	done
}

RDEPENDS_${PN} = "bash"

# Some recipes are providing ${PN}-bash-completion packages
PACKAGES =+ "${PN}-extra"
FILES_${PN}-extra = "${datadir}/${BPN}/completions/ \
    ${datadir}/${BPN}/helpers/"

FILES_${PN}-dev += "${datadir}/cmake"

BBCLASSEXTEND = "nativesdk"

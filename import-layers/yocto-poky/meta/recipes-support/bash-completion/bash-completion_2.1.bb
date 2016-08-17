SUMMARY = "Programmable Completion for Bash 4"
HOMEPAGE = "http://bash-completion.alioth.debian.org/"
BUGTRACKER = "https://alioth.debian.org/projects/bash-completion/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SECTION = "console/utils"

SRC_URI="http://bash-completion.alioth.debian.org/files/${BPN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "4e2a9f11a4042a38ee79ddcd048e8b9e"
SRC_URI[sha256sum] = "2b606804a7d5f823380a882e0f7b6c8a37b0e768e72c3d4107c51fbe8a46ae4f"
UPSTREAM_CHECK_REGEX = "bash-completion-(?P<pver>(?!2008).+)\.tar"

PARALLEL_MAKE = ""

inherit autotools

do_install_append() {
	# compatdir
	install -d ${D}${sysconfdir}/bash_completion.d/
	echo '. ${datadir}/${BPN}/bash_completion' >${D}${sysconfdir}/bash_completion

	# Delete files already provided by util-linux
	local i
	for i in cal dmesg eject hexdump hwclock ionice look renice rtcwake su; do
		rm ${D}${datadir}/${BPN}/completions/$i
	done

	# Delete files for networkmanager
	rm ${D}${datadir}/${BPN}/completions/nmcli
}

RDEPENDS_${PN} = "bash"

# Some recipes are providing ${PN}-bash-completion packages
PACKAGES =+ "${PN}-extra"
FILES_${PN}-extra = "${datadir}/${BPN}/completions/ \
    ${datadir}/${BPN}/helpers/"

BBCLASSEXTEND = "nativesdk"

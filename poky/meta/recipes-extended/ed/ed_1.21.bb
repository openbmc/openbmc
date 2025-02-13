SUMMARY = "Line-oriented text editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"
DESCRIPTION = "GNU ed is a line-oriented text editor. It is used to create, display, modify and otherwise manipulate text files, both interactively and via shell scripts. A restricted version of ed, red, can only edit files in the current directory and cannot execute shell commands."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=cca7f74ec83b7a9ce7ccd195aad471bd \
                    file://ed.h;endline=20;md5=a24e7f91c0fb83e65a746f5994762a49 \
                    file://main.c;endline=17;md5=fd6ad9f1853f123f4ae7a31d59761f09 \
                    "

SECTION = "base"

CVE_PRODUCT = "gnu:ed"

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

# Upstream regularly removes previous releases from https://ftp.gnu.org/gnu/ed/
SRC_URI = "${GNU_MIRROR}/ed/${BP}.tar.lz"
UPSTREAM_CHECK_URI = "${GNU_MIRROR}/ed/"

SRC_URI[sha256sum] = "60e24998727d453a5cf02c54664b97536de46a2b34cd1f4f67c1c1a61bbbad75"

EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit texinfo

do_configure() {
	${S}/configure
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
	# Info dir listing isn't interesting at this point so remove it if it exists.
	if [ -e "${D}${infodir}/dir" ]; then
		rm -f ${D}${infodir}/dir
	fi
}

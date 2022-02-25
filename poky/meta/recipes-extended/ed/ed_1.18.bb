SUMMARY = "Line-oriented text editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"
DESCRIPTION = "GNU ed is a line-oriented text editor. It is used to create, display, modify and otherwise manipulate text files, both interactively and via shell scripts. A restricted version of ed, red, can only edit files in the current directory and cannot execute shell commands."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=76d6e300ffd8fb9d18bd9b136a9bba13 \
                    file://ed.h;endline=20;md5=6e6a818b0593f937fc63ba08d5e314bf \
                    file://main.c;endline=17;md5=8419a08bb12936f32384a0d1c0f9e74c \
                    "

SECTION = "base"

CVE_PRODUCT = "gnu:ed"

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

# Upstream regularly removes previous releases from https://ftp.gnu.org/gnu/ed/
SRC_URI = "${GNU_MIRROR}/ed/${BP}.tar.lz"
UPSTREAM_CHECK_URI = "${GNU_MIRROR}/ed/"

SRC_URI[sha256sum] = "aca8efad9800c587724a20b97aa8fc47e6b5a47df81606feaba831b074462b4f"

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

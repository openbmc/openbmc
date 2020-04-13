SUMMARY = "Line-oriented text editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7 \
                    file://ed.h;endline=20;md5=0226a8dd88c76afba773f2f0f7c83f5e \
                    file://main.c;endline=17;md5=ebd4aff86dc9fa5027d55bc5191746b9 \
                    "

SECTION = "base"

CVE_PRODUCT = "gnu:ed"

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

# Upstream regularly removes previous releases from https://ftp.gnu.org/gnu/ed/
SRC_URI = "${GNU_MIRROR}/ed/${BP}.tar.lz"
UPSTREAM_CHECK_URI = "${GNU_MIRROR}/ed/"

SRC_URI[md5sum] = "ab480d982289064ca040bc5c75fceffd"
SRC_URI[sha256sum] = "cfc07a14ab048a758473ce222e784fbf031485bcd54a76f74acfee1f390d8b2c"

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

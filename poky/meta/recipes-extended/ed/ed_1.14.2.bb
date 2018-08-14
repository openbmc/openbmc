SUMMARY = "Line-oriented text editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7 \
                    file://ed.h;endline=20;md5=4e36b7a40e137f42aee718165590d125 \
                    file://main.c;endline=17;md5=c5b8f78f115df187af76868a2aead16a"

SECTION = "base"

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

# Upstream regularly removes previous releases from https://ftp.gnu.org/gnu/ed/
SRC_URI = "${GNU_MIRROR}/ed/${BP}.tar.lz"
UPSTREAM_CHECK_URI = "${GNU_MIRROR}/ed/"

SRC_URI[md5sum] = "273d04778b2a51f7c3cbfcd2001876bf"
SRC_URI[sha256sum] = "f57962ba930d70d02fc71d6be5c5f2346b16992a455ab9c43be7061dec9810db"

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

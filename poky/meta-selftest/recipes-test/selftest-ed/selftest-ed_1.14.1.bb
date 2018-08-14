SUMMARY = "Line-oriented text editor -- selftest variant"
HOMEPAGE = "http://www.gnu.org/software/ed/"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7 \
                    file://ed.h;endline=20;md5=4e36b7a40e137f42aee718165590d125 \
                    file://main.c;endline=17;md5=c5b8f78f115df187af76868a2aead16a"

SECTION = "base"

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

SRC_URI = "${GNU_MIRROR}/ed/ed-${PV}.tar.lz"
RECIPE_NO_UPDATE_REASON = "This recipe is used in selftest and shouldn't be updated otherwise"

SRC_URI[md5sum] = "7f4a54fa7f366479f03654b8af645fd0"
SRC_URI[sha256sum] = "ffb97eb8f2a2b5a71a9b97e3872adce953aa1b8958e04c5b7bf11d556f32552a"

S = "${WORKDIR}/ed-${PV}"

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

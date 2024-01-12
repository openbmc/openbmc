SUMMARY = "Additional utilities for the opkg package manager"
SUMMARY:update-alternatives-opkg = "Utility for managing the alternatives system"
SECTION = "base"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://opkg.py;beginline=2;endline=18;md5=ffa11ff3c15eb31c6a7ceaa00cc9f986"
PROVIDES += "${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'virtual/update-alternatives', '', d)}"

SRC_URI = "git://git.yoctoproject.org/opkg-utils;protocol=https;branch=master \
           file://0001-update-alternatives-correctly-match-priority.patch \
           file://0001-opkg-build-remove-numeric-owner-parameter-overzealou.patch \
           "
SRCREV = "589880d01969eb9af1e66120e731d43193504718"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

RDEPENDS:${PN} += "bash"

inherit perlnative

# For native builds we use the host Python
PYTHONRDEPS = "python3 python3-shell python3-io python3-math python3-crypt python3-logging python3-fcntl python3-pickle python3-compression python3-stringold"
PYTHONRDEPS:class-native = ""

PACKAGECONFIG = "python update-alternatives"
PACKAGECONFIG[python] = ",,,${PYTHONRDEPS}"
PACKAGECONFIG[update-alternatives] = ",,,"

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'true', 'false', d)}; then
		rm -f "${D}${bindir}/update-alternatives"
	fi
}

do_install:append:class-target() {
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
		grep -lZ "/usr/bin/env.*python" ${D}${bindir}/* | xargs -0 rm
	fi

	if [ -e "${D}${bindir}/update-alternatives" ]; then
		sed -i ${D}${bindir}/update-alternatives -e 's,/usr/bin,${bindir},g; s,/usr/lib,${nonarch_libdir},g'
	fi
}

# These are empty and will pull python3-dev into images where it wouldn't
# have been otherwise, so don't generate them.
PACKAGES:remove = "${PN}-dev ${PN}-staticdev"

PACKAGES =+ "update-alternatives-opkg"
FILES:update-alternatives-opkg = "${bindir}/update-alternatives"
RPROVIDES:update-alternatives-opkg = "update-alternatives update-alternatives-cworth"
RREPLACES:update-alternatives-opkg = "update-alternatives-cworth"
RCONFLICTS:update-alternatives-opkg = "update-alternatives-cworth"

pkg_postrm:update-alternatives-opkg() {
	rm -rf $D${nonarch_libdir}/opkg/alternatives
	rmdir $D${nonarch_libdir}/opkg || true
}

BBCLASSEXTEND = "native nativesdk"

CLEANBROKEN = "1"

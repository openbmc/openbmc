SUMMARY = "User-space tools for LinuxPPS"
HOMEPAGE = "http://linuxpps.org"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "c50cb7183e252b47308fa22f420e0a877277aa29"
SRC_URI = "git://github.com/ago/pps-tools.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = "bash"

do_install() {
        install -d ${D}${bindir} ${D}${includedir} \
                   ${D}${includedir}/sys
        oe_runmake 'DESTDIR=${D}' install
}

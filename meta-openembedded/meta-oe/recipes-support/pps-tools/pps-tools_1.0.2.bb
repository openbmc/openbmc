SUMMARY = "User-space tools for LinuxPPS"
HOMEPAGE = "http://linuxpps.org"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "cb48b7ecf7079ceba7081c78d4e61e507b0e8d2d"
SRC_URI = "git://github.com/ago/pps-tools.git"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "bash"

do_install() {
        install -d ${D}${bindir} ${D}${includedir} \
                   ${D}${includedir}/sys
        oe_runmake 'DESTDIR=${D}' install
}

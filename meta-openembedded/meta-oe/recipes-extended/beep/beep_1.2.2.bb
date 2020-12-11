DESCRIPTION = "beep is a command line tool for linux that beeps the PC speaker"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "\
    http://johnath.com/beep/beep-${PV}.tar.gz \
    file://linux-input.patch \
"

SRC_URI[sha256sum] = "5c0445dac43950b7c7c3f235c6fb21f620ab3fd2f3aafaf09896e5730fcf49a1"

S = "${WORKDIR}/${BPN}-${PV}"

EXTRA_OEMAKE += 'CC="${CC}"'
EXTRA_OEMAKE += 'FLAGS="${CFLAGS} ${LDFLAGS}"'

do_configure[noexec] = "1"

do_compile() {
   oe_runmake
}

do_install() {
	install -Dm 0755 ${B}/${PN} ${D}${bindir}/${PN}
}

SUMMARY = "A Pseudorandom Number Sequence Test Program"
DESCRIPTION = "The program is useful for evaluating pseudorandom number generators for encryption and statistical sampling applications, compression algorithms, and other applications where the information density of a file is of interest."
HOMEPAGE = "https://www.fourmilab.ch/random/"

DEPENDS += "busybox"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://${S}/ent.c;endline=24;md5=376f7f7194e74c2639d66ef5f0309ef7"
SRC_URI = "file://ent.tar.bz2"

S = "${WORKDIR}/${PN}"
EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX} CC="${CC}""
INSANE_SKIP:${PN} += "ldflags"

do_install () {
	install -d ${D}${bindir}/
	install ent ${D}${bindir}
}

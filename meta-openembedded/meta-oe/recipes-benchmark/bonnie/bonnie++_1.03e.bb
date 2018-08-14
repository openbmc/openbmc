SUMMARY = "Tests large file IO and creation/deletion of small files"
HOMEPAGE = "http://www.coker.com.au/bonnie++/"
SECTION = "benchmark/tests"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://copyright.txt;md5=cd4dde95a6b9d122f0a9150ae9cc3ee0"

SRC_URI = "http://www.coker.com.au/bonnie++/${BPN}-${PV}.tgz \
           file://gcc-4.3-fixes.patch \
"
SRC_URI[md5sum] = "750aa5b5051263a99c6c195888c74968"
SRC_URI[sha256sum] = "cb3866116634bf65760b6806be4afa7e24a1cad6f145c876df8721f01ba2e2cb"

inherit autotools-brokensep

SCRIPTS = "bon_csv2html bon_csv2txt"
EXES = "bonnie++ zcav"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
    install -d ${D}/${bindir}
    install -d ${D}/${sbindir}
    install -m 0755 ${EXES} ${D}/${sbindir}
    install -m 0755 ${SCRIPTS} ${D}/${bindir}
}

PACKAGES =+ "bonnie-scripts"

FILES_${PN} = "${sbindir}"
FILES_bonnie-scripts = "${bindir}"

RDEPENDS_bonnie-scripts += "perl"

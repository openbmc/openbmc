SUMMARY = "Tests large file IO and creation/deletion of small files"
HOMEPAGE = "http://www.coker.com.au/bonnie++/"
SECTION = "benchmark/tests"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://copyright.txt;md5=cd4dde95a6b9d122f0a9150ae9cc3ee0"

SRC_URI = "http://www.coker.com.au/bonnie++/${BPN}_${PV}.tgz \
"
SRC_URI[md5sum] = "1414aab86e2db1b4691bd4be82031012"
SRC_URI[sha256sum] = "507bd2ce5360c7c98b91b0fdc2bed5c9320b5c9699f7d4a3d1c86b256896c15e"

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

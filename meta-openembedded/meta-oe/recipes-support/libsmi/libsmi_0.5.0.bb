SUMMARY = "A Library to Access SMI MIB Information"
HOMEPAGE = "https://www.ibr.cs.tu-bs.de/projects/libsmi"

LICENSE = "BSD-3-Clause & TCL"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ad3076f9332343a21636cfd351f05b7"

SRC_URI = "https://www.ibr.cs.tu-bs.de/projects/${BPN}/download/${BP}.tar.gz \
           file://smi.conf \
           file://libsmi-fix-the-test-dump-files.patch \
           file://0001-Define-createIdentifierRef-prototype-in-yang-complex.patch \
           file://0001-parser-yang-Define-_DEFAULT_SOURCE.patch \
          "

SRC_URI[md5sum] = "4bf47483c06c9f07d1b10fbc74eddf11"
SRC_URI[sha256sum] = "f21accdadb1bb328ea3f8a13fc34d715baac6e2db66065898346322c725754d3"

DEPENDS += "bison-native flex-native wget-native gawk-native"

inherit autotools-brokensep update-alternatives
ALTERNATIVE_PRIORITY = "50"
ALTERNATIVE:${PN}-yang = "ietf-interfaces "
ALTERNATIVE_LINK_NAME[ietf-interfaces] = "${datadir}/yang/ietf-interfaces.yang"

EXTRA_OECONF = "ac_cv_path_SH=/bin/sh"

do_install:append () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/smi.conf ${D}${sysconfdir}/smi.conf
}

PACKAGES += "${PN}-mibs ${PN}-pibs ${PN}-yang"

FILES:${PN}-mibs += "${datadir}/mibs"
FILES:${PN}-pibs += "${datadir}/pibs"
FILES:${PN}-yang += "${datadir}/yang"

RRECOMMENDS:${PN} = "${BPN}-mibs"

BBCLASSEXTEND = "native nativesdk"

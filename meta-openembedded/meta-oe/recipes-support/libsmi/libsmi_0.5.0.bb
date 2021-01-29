SUMMARY = "A Library to Access SMI MIB Information"
HOMEPAGE = "https://www.ibr.cs.tu-bs.de/projects/libsmi"

LICENSE = "BSD-3-Clause & tcl"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ad3076f9332343a21636cfd351f05b7"

SRC_URI = "https://www.ibr.cs.tu-bs.de/projects/${BPN}/download/${BP}.tar.gz \
           file://smi.conf \
           file://libsmi-fix-the-test-dump-files.patch \
          "

SRC_URI[md5sum] = "4bf47483c06c9f07d1b10fbc74eddf11"
SRC_URI[sha256sum] = "f21accdadb1bb328ea3f8a13fc34d715baac6e2db66065898346322c725754d3"

RDEPENDS_${PN} += "wget"

inherit autotools

EXTRA_OECONF = "ac_cv_path_SH=${base_bindir}/sh ac_cv_path_WGET=${bindir}/wget ac_cv_path_AWK=${bindir}/awk"

do_install_append () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/smi.conf ${D}${sysconfdir}/smi.conf
}

PACKAGES += "${PN}-mibs ${PN}-pibs ${PN}-yang"

FILES_${PN}-mibs += "${datadir}/mibs"
FILES_${PN}-pibs += "${datadir}/pibs"
FILES_${PN}-yang += "${datadir}/yang"

RRECOMMENDS_${PN} = "${BPN}-mibs"

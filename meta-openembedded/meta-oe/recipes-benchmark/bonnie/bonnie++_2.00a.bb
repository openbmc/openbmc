SUMMARY = "Tests large file IO and creation/deletion of small files"
HOMEPAGE = "https://doc.coker.com.au/projects/bonnie/"
SECTION = "benchmark/tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://copyright.txt;md5=cd4dde95a6b9d122f0a9150ae9cc3ee0"

SRC_URI = "\
    http://www.coker.com.au/bonnie++/${BPN}-${PV}.tgz \
    file://fix-configure-lfs.patch \
    file://fix-csv2html-data.patch \
    file://makefile-use-link-for-helper.patch \
"
SRC_URI[sha256sum] = "a8d33bbd81bc7eb559ce5bf6e584b9b53faea39ccfb4ae92e58f27257e468f0e"

# force lfs to skip configure's check, because we are cross-building
PACKAGECONFIG ?= "lfs"
PACKAGECONFIG[lfs] = "--enable-lfs,--disable-lfs"

inherit autotools

EXTRA_OECONF += "--disable-stripping"
EXTRA_OEMAKE += "-I ${S} VPATH=${S}"
CXXFLAGS += "-I ${S}"

do_install() {
    oe_runmake eprefix='${D}${exec_prefix}' install-bin
}

PACKAGE_BEFORE_PN += "${PN}-scripts"

FILES:${PN}-scripts = "${bindir}/bon_csv2*"

RDEPENDS:${PN}-scripts += "perl"

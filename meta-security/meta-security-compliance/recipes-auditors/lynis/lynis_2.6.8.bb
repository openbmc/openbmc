# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Lynis is a free and open source security and auditing tool."
HOMEDIR = "https://cisofy.com/"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3edd6782854304fd11da4975ab9799c1"

SRC_URI = "https://cisofy.com/files/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "91a538055bfb682733ef8e4fe7eb0902"
SRC_URI[sha256sum] = "2e4c5157a4f2d9bb37d3f0f1f5bea03f92233a2a7d4df6eddf231a784087dfac"

S = "${WORKDIR}/${BPN}"

inherit autotools-brokensep

do_compile[noexec] = "1"
do_configure[noexec] = "1"

do_install () {
	install -d ${D}/${bindir}
	install -d ${D}/${sysconfdir}/lynis
	install -m 555 ${S}/lynis ${D}/${bindir}

	install -d ${D}/${datadir}/lynis/db
	install -d ${D}/${datadir}/lynis/plugins
	install -d ${D}/${datadir}/lynis/include
	install -d ${D}/${datadir}/lynis/extras

	cp -r ${S}/db/* ${D}/${datadir}/lynis/db/.
	cp -r ${S}/plugins/*  ${D}/${datadir}/lynis/plugins/.
	cp -r ${S}/include/* ${D}/${datadir}/lynis/include/.
	cp -r ${S}/extras/*  ${D}/${datadir}/lynis/extras/.
        cp ${S}/*.prf ${D}/${sysconfdir}/lynis
}

FILES_${PN} += "${sysconfdir}/developer.prf ${sysconfdir}/default.prf"
FILES_${PN}-doc += "lynis.8 FAQ README CHANGELOG.md CONTRIBUTIONS.md CONTRIBUTORS.md" 

RDEPENDS_${PN} += "procps"

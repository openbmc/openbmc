# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Lynis is a free and open source security and auditing tool."
HOMEDIR = "https://cisofy.com/"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3edd6782854304fd11da4975ab9799c1"

SRC_URI = "https://cisofy.com/files/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "3422cee3b12fc33338fcde003d65e234"
SRC_URI[sha256sum] = "fde6ccf8d6ec0ae1e9c9f4a6d640cddcde4bf7a92f8437d47d16a5477e21bfda"

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

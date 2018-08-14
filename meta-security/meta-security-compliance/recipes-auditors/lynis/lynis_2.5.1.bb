# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Lynis is a free and open source security and auditing tool."
HOMEDIR = "https://cisofy.com/"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3edd6782854304fd11da4975ab9799c1"

SRC_URI = "https://cisofy.com/files/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "5b9da89c616344bbc73cbc5688a4a0bd"
SRC_URI[sha256sum] = "7a09c6fc71c65d572ca702df7b4394d71f9037484062ef71b76f59a2c498b029"

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

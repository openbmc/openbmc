SUMMARY = "Device tree lopper"
DESCRIPTION = "Tool to subset a system device tree"
SECTION = "bootloader"
LICENSE = "BSD-3-Clause"
DEPENDS += "python3-dtc"

RDEPENDS_${PN} += "python3-core python3-dtc"

SRC_URI = "git://github.com/devicetree-org/lopper.git"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8e5f5f691f01c9fdfa7a7f2d535be619"

SRCREV = "f4389167a200c5d41ee276ff9ad67d01ef6f0aec"

S = "${WORKDIR}/git"

do_configure() {
	:
}

do_compile() {
	sed -i 's,#!/usr/bin/python3,#!/usr/bin/env python3,' lopper.py
	sed -i 's,#!/usr/bin/python3,#!/usr/bin/env python3,' lopper_sanity.py
}

do_install() {
		datadirrelpath=${@os.path.relpath(d.getVar('datadir'), d.getVar('bindir'))}

		mkdir -p ${D}/${bindir}
		mkdir -p ${D}/${datadir}/lopper

		cp -r ${S}/README* ${D}/${datadir}/lopper/.
		cp -r ${S}/assists* ${D}/${datadir}/lopper/.
		cp -r ${S}/lop* ${D}/${datadir}/lopper/.
		cp -r ${S}/LICENSE* ${D}/${datadir}/lopper/.
		cp -r ${S}/device-tree* ${D}/${datadir}/lopper/.
		cp -r ${S}/.gitignore ${D}/${datadir}/lopper/.

        ln -s ${datadirrelpath}/lopper/lopper.py ${D}/${bindir}/.
}

BBCLASSEXTEND = "native nativesdk"

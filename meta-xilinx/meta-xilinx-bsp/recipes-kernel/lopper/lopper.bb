SUMMARY = "Device tree lopper"
DESCRIPTION = "Tool to subset a system device tree"
SECTION = "bootloader"
LICENSE = "BSD-3-Clause"
DEPENDS += "python3-dtc"

RDEPENDS_${PN} += "python3-core python3-dtc"

SRC_URI = "git://github.com/devicetree-org/lopper.git"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=8e5f5f691f01c9fdfa7a7f2d535be619"

SRCREV = "9398385d3ac06419b25d34de21501bc7ac0e8ac3"

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
	cp -r ${S}/* ${D}/${datadir}/lopper/.
        ln -s ${datadirrelpath}/lopper/lopper.py ${D}/${bindir}/.
}

BBCLASSEXTEND = "native nativesdk"

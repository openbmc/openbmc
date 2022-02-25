SUMMARY = "Hardware latency detector"
DESCRIPTION = "Python utility for controlling the kernel hardware latency detection module (hwlat_detector.ko)."
HOMEPAGE = "https://rt.wiki.kernel.org/index.php/Cyclictest"
SECTION = "tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

require rt-tests.inc
inherit python3-dir

EXTRA_OEMAKE += "PYLIB=${libdir}/python${PYTHON_BASEVERSION}/dist-packages"

do_compile() {
	oe_runmake hwlatdetect
}

do_install() {
        oe_runmake install_hwlatdetect DESTDIR=${D} SBINDIR=${sbindir} \
	           MANDIR=${mandir} INCLUDEDIR=${includedir}

        sed -i -e '1s,#!.*python.*,#!${bindir}/python3,' ${D}${libdir}/python${PYTHON_BASEVERSION}/dist-packages/hwlatdetect.py
}

FILES:${PN} += "${libdir}/python${PYTHON_BASEVERSION}/dist-packages/hwlatdetect.py"
RDEPENDS:${PN} = "python3-core "
RRECOMMENDS:${PN} = "kernel-module-hwlat-detector"

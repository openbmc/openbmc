DESCRIPTION = "JavaScript minifier."
HOMEPAGE = "https://github.com/tikitu/jsmin/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3a3301ce2ad647e172f4a1016c67324d"

inherit setuptools3 pypi ptest
SRC_URI[sha256sum] = "c0959a121ef94542e807a674142606f7e90214a2b3d1eb17300244bbb5cc2bfc"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
  file://7a75d76c2d6bfb917f30ced8f5c0a9a4157f7819.patch \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
"

do_install_ptest() {
	cp -f ${S}/jsmin/test.py ${D}${PTEST_PATH}/
}

SUMMARY = "Python LR parsing library"
SECTION = "devel/python"
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://agpl-3.0.txt;md5=73f1eb20517c55bf9493b7dd6e480788"

HOMEPAGE = "http://lrparsing.sourceforge.net/"
BUGTRACKER = "https://sourceforge.net/p/lrparsing/tickets/"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/lrparsing/files/"
UPSTREAM_CHECK_REGEX = "lrparsing-(?P<pver>\d+(\.\d+)+)"
SRC_URI = "${SOURCEFORGE_MIRROR}/lrparsing/lrparsing-${PV}.tar.gz \
           file://0001-setup.py-use-setuptools-instead-of-distutils.patch \
           "
SRC_URI[sha256sum] = "7c060d9f03cf582fdbc0ae0fef0ea2ff6fd56251047ba7e425af97e23f46f582"

RDEPENDS:${PN} = " \
	python3-crypt \
"

inherit setuptools3

S = "${WORKDIR}/lrparsing-${PV}"

BBCLASSEXTEND = "native"

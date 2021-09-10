SUMMARY = "Python LR parsing library"
SECTION = "devel/python"
LICENSE = "AGPL-3.0"
LIC_FILES_CHKSUM = "file://agpl-3.0.txt;md5=73f1eb20517c55bf9493b7dd6e480788"

HOMEPAGE = "http://lrparsing.sourceforge.net/"
BUGTRACKER = "https://sourceforge.net/p/lrparsing/tickets/"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/lrparsing/files/"
UPSTREAM_CHECK_REGEX = "lrparsing-(?P<pver>\d+(\.\d+)+)"
SRC_URI = "${SOURCEFORGE_MIRROR}/lrparsing/lrparsing-${PV}.tar.gz"
SRC_URI[md5sum] = "34357d69bce87654d792cd8f02d148b2"
SRC_URI[sha256sum] = "b45afda44001dc5ba632934f74c043d40cce653f1a7526cfbcb68f6be055b8d7"

inherit setuptools3

S = "${WORKDIR}/lrparsing-${PV}"

BBCLASSEXTEND = "native"

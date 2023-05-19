SUMMARY = "A pure-python library for embedding CoSWID data"
HOMEPAGE = "https://github.com/hughsie/python-uswid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-or-later"

DEPENDS += " python3-cbor2 python3-lxml python3-pefile"
LIC_FILES_CHKSUM = "file://LICENSE;md5=40d2542b8c43a3ec2b7f5da31a697b88"

SRC_URI = "git://github.com/hughsie/python-uswid.git;branch=main;protocol=https"
SRCREV = "3223034abef88ae29cf79fdc7fe11ec7e21e11ff"
S = "${WORKDIR}/git"

inherit setuptools3 python3native

BBCLASSEXTEND = "native nativesdk"

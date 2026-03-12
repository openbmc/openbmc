SUMMARY = "JMESPath (pronounced 'james path') allows you to declaratively specify how to extract elements from a JSON document."
HOMEPAGE = "https://pypi.org/project/jmespath/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03b6f09850fb409684cafac03f85aff1"

SRC_URI[sha256sum] = "472c87d80f36026ae83c6ddd0f1d05d4e510134ed462851fd5f754c8c3cbb88d"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-math python3-json python3-numbers"

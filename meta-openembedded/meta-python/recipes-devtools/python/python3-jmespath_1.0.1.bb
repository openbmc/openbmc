SUMMARY = "JMESPath (pronounced 'james path') allows you to declaratively specify how to extract elements from a JSON document."
HOMEPAGE = "https://pypi.org/project/jmespath/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2683790f5fabb41a3f75b70558799eb4"

SRC_URI[sha256sum] = "90261b206d6defd58fdd5e85f478bf633a2901798906be2ad389150c5c60edbe"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-math python3-json python3-numbers"

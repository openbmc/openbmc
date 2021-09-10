SUMMARY = "JMESPath (pronounced 'james path') allows you to declaratively specify how to extract elements from a JSON document."
HOMEPAGE = "https://pypi.org/project/jmespath/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2683790f5fabb41a3f75b70558799eb4"

SRC_URI[md5sum] = "65bdcb5fa5bcf1cc710ffa508e78e408"
SRC_URI[sha256sum] = "b85d0567b8666149a93172712e68920734333c0ce7e89b78b3e987f71e5ed4f9"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-math python3-json python3-numbers"

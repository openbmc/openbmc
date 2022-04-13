SUMMARY = "JMESPath (pronounced 'james path') allows you to declaratively specify how to extract elements from a JSON document."
HOMEPAGE = "https://pypi.org/project/jmespath/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2683790f5fabb41a3f75b70558799eb4"

SRC_URI[sha256sum] = "a490e280edd1f57d6de88636992d05b71e97d69a26a19f058ecf7d304474bf5e"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-math python3-json python3-numbers"

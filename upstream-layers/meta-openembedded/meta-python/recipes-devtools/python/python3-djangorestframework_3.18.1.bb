SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "605d79fa2ec2f02905492e5ea13d903c2d842d0b4c915a57f7bf02ab9f3c91dd"


inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-django \
"

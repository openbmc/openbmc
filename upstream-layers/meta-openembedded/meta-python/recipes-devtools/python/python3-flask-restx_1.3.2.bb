DESCRIPTION = "Fully featured framework for fast, easy and documented API development with Flask"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c96dd911c6d9e32868b6bc667a38a3e2"

SRC_URI[sha256sum] = "0ae13d77e7d7e4dce513970cfa9db45364aef210e99022de26d2b73eb4dbced5"

CVE_PRODUCT = "flask-restx"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-aniso8601 \
    python3-jsonschema \
    python3-pytz \
"

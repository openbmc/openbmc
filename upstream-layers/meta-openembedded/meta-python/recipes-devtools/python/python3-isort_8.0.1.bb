SUMMARY = "A Python utility / library to sort Python imports."
HOMEPAGE = "https://pypi.python.org/pypi/isort"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=197c46995787b46a2dbf0d519c1754cf"

SRC_URI[sha256sum] = "171ac4ff559cdc060bcfff550bc8404a486fee0caab245679c2abe7cb253c78d"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native python3-hatchling-native"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-datetime \
    python3-difflib \
    python3-email \
    python3-numbers \
    python3-pprint \
    python3-profile \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"

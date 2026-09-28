SUMMARY = "CSS selector library for python-beautifulsoup4"
HOMEPAGE = "https://github.com/facelessuser/soupsieve"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=74851a2f1e5c07496dcb452af6a6bf54"

SRC_URI[sha256sum] = "e121fd02e975c695e4e9e8774a5ee35d74714b59307868dcc5319ad2d9e3328e"

inherit pypi python_hatchling ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-core \
    python3-datetime \
"
RDEPENDS:${PN}:append:class-target = " \
    python3-beautifulsoup4 \
"

RDEPENDS:${PN}-ptest += " \
    python3-beautifulsoup4 \
    python3-typing-extensions \
"

BBCLASSEXTEND = "native nativesdk"

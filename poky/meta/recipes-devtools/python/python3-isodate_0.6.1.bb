SUMMARY = "ISO 8601 date/time parser"
HOMEPAGE = "https://github.com/gweis/isodate/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[sha256sum] = "48c5881de7e8b0a0d648cb024c8062dc84e7b840ed81e864c7614fd3c127bde9"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-numbers \
    python3-six \
"

BBCLASSEXTEND = "native nativesdk"

SUMMARY = "Character encoding aliases for legacy web content"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[sha256sum] = "b36a1c245f2d304965eb4e0a82848379241dc04b865afcc4aab16748587e1923"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-codecs \
    python3-json \
"

BBCLASSEXTEND = "native nativesdk"

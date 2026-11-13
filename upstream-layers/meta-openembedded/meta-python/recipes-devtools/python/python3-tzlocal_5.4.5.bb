SUMMARY = "Library to return tzinfo with the local timezone information"
HOMEPAGE = "https://pypi.org/project/tzlocal/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=57e0bd61643d81d05683cdce65b11d10"

SRC_URI[sha256sum] = "ba976898bad27945178c9ca25c222c19e322b30c10342b9f40b46714f70d2d5f"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-logging \
    python3-zoneinfo \
"

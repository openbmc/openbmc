SUMMARY = "Job scheduling for humans"
HOMEPAGE = "https://github.com/dbader/schedule"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6400f153491d45ea3459761627ca24b2"

SRC_URI[sha256sum] = "843bc0538b99c93f02b8b50e3e39886c06f2d003b24f48e1aa4cadfa3f341279"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-datetime python3-logging python3-math"

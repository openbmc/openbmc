SUMMARY = "Job scheduling for humans"
HOMEPAGE = "https://github.com/dbader/schedule"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6400f153491d45ea3459761627ca24b2"

SRC_URI[sha256sum] = "15fe9c75fe5fd9b9627f3f19cc0ef1420508f9f9a46f45cd0769ef75ede5f0b7"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-datetime python3-logging python3-math"

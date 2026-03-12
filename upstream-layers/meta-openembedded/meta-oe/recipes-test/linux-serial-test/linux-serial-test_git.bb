DESCRIPTION = "Linux Serial Test Application"
HOMEPAGE = "https://github.com/cbrake/linux-serial-test"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT;md5=544799d0b492f119fa04641d1b8868ed"

SRC_URI = "git://github.com/cbrake/linux-serial-test.git;protocol=https;branch=master"
PV = "0+git"
SRCREV = "e3461097252e51fc527839884e77449cfd976701"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"


inherit cmake

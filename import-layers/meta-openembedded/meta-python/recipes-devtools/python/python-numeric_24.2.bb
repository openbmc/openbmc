SUMMARY = "A sophisticated Numeric Processing Package for Python"
SECTION = "devel/python"
LICENSE = "PSF & LLNL"
LIC_FILES_CHKSUM = "file://Legal.htm;md5=e3ce75dedd4043918d15979ae43e312e"

PR = "ml3"

SRC_URI = "${SOURCEFORGE_MIRROR}/numpy/Numeric-${PV}.tar.gz"
S = "${WORKDIR}/Numeric-${PV}"

inherit distutils

SRC_URI[md5sum] = "2ae672656e06716a149acb048cca3093"
SRC_URI[sha256sum] = "5f72e729eb6ff57442f2a38bfc9931738b59e5077928e2e70d22b4610ff15258"

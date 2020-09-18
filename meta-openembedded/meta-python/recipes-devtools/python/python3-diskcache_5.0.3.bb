DESCRIPTION = "Disk Cache -- Disk and file backed persistent cache."
HOMEPAGE = "http://www.grantjenks.com/docs/diskcache/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c985b6a9269e57a1073d5f142d68eb68"

SRC_URI[md5sum] = "86fb97b0ff87cc5f1b31654ee0d341a1"
SRC_URI[sha256sum] = "5f4bc2018d653a1d7bbdcdecce45ea12061bf8d3b5f0323b7a5402054a285c52"

PYPI_PACKAGE = "diskcache"

inherit pypi setuptools3

CLEANBROKEN = "1"


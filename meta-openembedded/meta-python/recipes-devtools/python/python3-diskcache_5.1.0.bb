DESCRIPTION = "Disk Cache -- Disk and file backed persistent cache."
HOMEPAGE = "http://www.grantjenks.com/docs/diskcache/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c985b6a9269e57a1073d5f142d68eb68"

SRC_URI[md5sum] = "9ce87661369752c9ead63243b07eaf19"
SRC_URI[sha256sum] = "bc7928df986dbc8a8d6e34c33b0da89d668cfa65e7fcc91298a6959a35076993"

PYPI_PACKAGE = "diskcache"

inherit pypi setuptools3

CLEANBROKEN = "1"


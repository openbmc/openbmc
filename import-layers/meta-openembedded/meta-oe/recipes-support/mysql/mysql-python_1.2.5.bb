SUMMARY = "Python interface to MySQL"
HOMEPAGE = "https://github.com/farcepest/MySQLdb1"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://GPL-2.0;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "mysql5"

SRCNAME = "MySQL-python"

SRC_URI = "https://pypi.python.org/packages/source/M/${SRCNAME}/${SRCNAME}-${PV}.zip"
SRC_URI[md5sum] = "654f75b302db6ed8dc5a898c625e030c"
SRC_URI[sha256sum] = "811040b647e5d5686f84db415efd697e6250008b112b6909ba77ac059e140c74"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

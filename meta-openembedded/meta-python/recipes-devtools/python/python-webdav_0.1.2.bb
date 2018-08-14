DESCRIPTION = "This project aims to provide an object-oriented Python WebDAV client-side library\
               based on Python`s standard httplib and Greg Stein`s davlib.\
               The client shall fully support RFCs 4918 (basic specification),\
               3744 (access control), and 3253 (versioning)."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/webdav/WebdavClient.py;endline=15;md5=a1520fad859feb7b0d7f05277bc6ce48"
HOMEPAGE = "http://sourceforge.net/projects/pythonwebdavlib/"
SRCNAME = "Python_WebDAV_Library"
DEPENDS = "python"

SRC_URI = "${SOURCEFORGE_MIRROR}/pythonwebdavlib/Python%20WebDAV%20Library%20-%20${PV}/${SRCNAME}-${PV}.zip"
SRC_URI[md5sum] = "8e49e0ecc5b4327c4f752a544ee10e1a"
SRC_URI[sha256sum] = "72c029ad1e25de950f59c2f1812d009d2c1691b70e4b5b09f1af9006e8fd5f23"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils


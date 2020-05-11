SUMMARY = "in-depth comparison of files, archives, and directories"
HOMEPAGE = "https://diffoscope.org/"
LICENSE = "GPL-3.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PYPI_PACKAGE = "diffoscope"

inherit pypi setuptools3

SRC_URI[md5sum] = "a86cf8467a5dec99832d5c082928f937"
SRC_URI[sha256sum] = "a1fb4dd97af92bb8ce275d1caf465971726919e28efe5f043bd3ea9afb50574a"

RDEPENDS_${PN} += "binutils vim squashfs-tools python3-libarchive-c python3-magic"

# Dependencies don't build for musl
COMPATIBLE_HOST_libc-musl = 'null'

BBCLASSEXTEND = "native"

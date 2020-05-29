SUMMARY = "in-depth comparison of files, archives, and directories"
HOMEPAGE = "https://diffoscope.org/"
LICENSE = "GPL-3.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PYPI_PACKAGE = "diffoscope"

inherit pypi setuptools3

SRC_URI[md5sum] = "4ee9d1a36086caa31ccbc6300ad31652"
SRC_URI[sha256sum] = "9a45464b7b7184fa1ad2af9c52ebac8f00b3dd5dcf9e15dfc00c653c26fcc345"

RDEPENDS_${PN} += "binutils vim squashfs-tools python3-libarchive-c python3-magic"

# Dependencies don't build for musl
COMPATIBLE_HOST_libc-musl = 'null'

BBCLASSEXTEND = "native"

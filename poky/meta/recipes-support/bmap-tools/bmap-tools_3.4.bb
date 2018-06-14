SUMMARY = "Tools to generate block map (AKA bmap) and flash images using bmap"
DESCRIPTION = "Bmap-tools - tools to generate block map (AKA bmap) and flash images using \
bmap. Bmaptool is a generic tool for creating the block map (bmap) for a file, \
and copying files using the block map. The idea is that large file containing \
unused blocks, like raw system image files, can be copied or flashed a lot \
faster with bmaptool than with traditional tools like "dd" or "cp"."
HOMEPAGE = "https://github.com/01org/bmap-tools"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://github.com/intel/${BPN}/releases/download/v${PV}/${BP}.tgz"

SRC_URI[md5sum] = "883420183611aa137cbaf68a91ef0d7e"
SRC_URI[sha256sum] = "0064891270c180c190f41925b0f6fb9cecac3056f1168d2592dbe90e5f226c0a"

UPSTREAM_CHECK_URI = "https://github.com/intel/${BPN}/releases"

RDEPENDS_${PN} = "python3-core python3-compression python3-mmap python3-setuptools python3-fcntl"

inherit python3native
inherit setuptools3

BBCLASSEXTEND = "native"

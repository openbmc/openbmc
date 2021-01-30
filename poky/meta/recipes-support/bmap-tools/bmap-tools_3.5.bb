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

SRC_URI = "git://github.com/intel/${BPN}"

SRCREV = "db7087b883bf52cbff063ad17a41cc1cbb85104d"
S = "${WORKDIR}/git"
PV .= "+git${SRCPV}"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

RDEPENDS_${PN} = "python3-core python3-compression python3-mmap python3-setuptools python3-fcntl python3-six"

inherit python3native
inherit setuptools3

BBCLASSEXTEND = "native nativesdk"

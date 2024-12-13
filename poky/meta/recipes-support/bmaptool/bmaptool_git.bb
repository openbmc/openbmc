SUMMARY = "Tools to generate block map (AKA bmap) and flash images using bmap"
DESCRIPTION = "bmaptool - tools to generate block map (AKA bmap) and flash images using \
bmap. bmaptool is a generic tool for creating the block map (bmap) for a file, \
and copying files using the block map. The idea is that large file containing \
unused blocks, like raw system image files, can be copied or flashed a lot \
faster with bmaptool than with traditional tools like "dd" or "cp"."
HOMEPAGE = "https://github.com/yoctoproject/bmaptool"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/yoctoproject/${BPN};branch=main;protocol=https"
SRCREV = "2ff5750b8a3e0b36a9993c20e2ea10a07bc62085"
S = "${WORKDIR}/git"
BASEVER = "3.8.0"
PV = "${BASEVER}+git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

# Need df from coreutils
RDEPENDS:${PN} = "python3-core python3-compression python3-misc python3-mmap python3-setuptools python3-fcntl python3-six coreutils"

inherit setuptools3

# For compatibility with layers before scarthgap
RREPLACES:${PN} = "bmap-tools"
RCONFLICTS:${PN} = "bmap-tools"

# Poetry backend appears incomplete, upstream has moved to hatch
INSANE_SKIP = "pep517-backend"

BBCLASSEXTEND = "native nativesdk"

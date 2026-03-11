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
SRCREV = "618a7316102f6f81faa60537503012a419eafa06"

# TODO: remove when we upgrade to a release past 3.9.0
PV .= "+git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

# Need df from coreutils
RDEPENDS:${PN} = "python3-core python3-compression python3-misc python3-mmap python3-setuptools python3-fcntl coreutils"

inherit python_hatchling

# For compatibility with layers before scarthgap
RREPLACES:${PN} = "bmap-tools"
RCONFLICTS:${PN} = "bmap-tools"

BBCLASSEXTEND = "native nativesdk"

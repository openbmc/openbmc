PV = "git${SRCPV}"
PV = "2.0.22+git${SRCPV}"

MLX_IPMID_BRANCH = "openbmc"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI = "git://github.com/mellanoxbmc/ipmi.git;protocol=git;branch=${MLX_IPMID_BRANCH};"
SRC_URI += "file://mlx_ipmid.service"
SRC_URI += "file://remove_libdir.patch"

SRCREV = "${AUTOREV}"

require mlx-ipmid.inc

DESCRIPTION = "Set routing metrics for a network interface"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://0pointer.de/lennart/projects/ifmetric/ifmetric-${PV}.tar.gz \
	   file://ifmetric.8_typo.patch \
	   file://ifmetric.c_netlink-invalid-arg.patch \
	   file://ifmetric.c_typo.patch \
	   file://nlrequest.c_packet-too-small_fix.patch"
SRC_URI[md5sum] = "74aa3f5ee8aca16a87e124ddcc64fa36"
SRC_URI[sha256sum] = "0fa8510a4e34e555f136f9df81d26618313f2d69a4880c0fb5967f19502f1aec"

inherit autotools

# disable lynx support for now
EXTRA_OECONF = "--disable-lynx"

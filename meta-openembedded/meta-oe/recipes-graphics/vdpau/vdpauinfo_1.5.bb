DESCRIPTION = "Tool to query the capabilities of a VDPAU implementation"
HOMEPAGE = "https://gitlab.freedesktop.org/vdpau/vdpauinfo"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5b6e110c362fe46168199f3490e52c3c"

DEPENDS = "libvdpau"

# libvdpau is available only with x11
inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS:${PN} = "libvdpau"

SRCREV = "d3c5bd63bf8878d59b22d618d2bb5116db392d28"
SRC_URI = "git://anongit.freedesktop.org/vdpau/vdpauinfo;branch=master"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

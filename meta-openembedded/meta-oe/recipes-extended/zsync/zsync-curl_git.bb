SUMMARY = "File transfer tool optimised for large files (curl port)"
HOMEPAGE = "http://zsync.moria.org.uk/"
DEPENDS = "curl"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://src/COPYING;md5=71c0ac4d86266533509aa0825b8d323c"

SRC_URI = "git://github.com/probonopd/zsync-curl;protocol=https;branch=master \
           file://fixes.patch \
           file://make.patch"
SRCREV = "00141c2806ccc4ddf2ff6263ee1612d19c0b713f"

PV = "0.6.2+git"

inherit autotools

S = "${WORKDIR}/git"
AUTOTOOLS_SCRIPT_PATH = "${S}/src"

BBCLASSEXTEND = "native nativesdk"

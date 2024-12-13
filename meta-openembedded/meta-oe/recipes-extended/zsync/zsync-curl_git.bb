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

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

inherit autotools

S = "${WORKDIR}/git"
AUTOTOOLS_SCRIPT_PATH = "${S}/src"

BBCLASSEXTEND = "native nativesdk"

# http://errors.yoctoproject.org/Errors/Details/766891/
# git/src/libzsync/zsync.c:445:18: error: returning 'char **' from a function with incompatible return type 'const char * const*' [-Wincompatible-pointer-types]
# git/src/libzsync/zsync.c:450:18: error: returning 'char **' from a function with incompatible return type 'const char * const*' [-Wincompatible-pointer-types]
# git/src/libzsync/zsync.c:932:43: error: passing argument 4 of 'zsync_configure_zstream_for_zdata' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"

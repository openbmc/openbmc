DESCRIPTION = "OpenJPEG library is an open-source JPEG 2000 codec"
HOMEPAGE = "http://www.openjpeg.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c648878b4840d7babaade1303e7f108c"

DEPENDS = "libpng tiff lcms zlib"

SRC_URI = " \
    git://github.com/uclouvain/openjpeg.git;branch=master;protocol=https \
    file://0002-Do-not-ask-cmake-to-export-binaries-they-don-t-make-.patch \
    file://CVE-2019-12973-1.patch \
    file://CVE-2019-12973-2.patch \
    file://CVE-2020-6851.patch \
    file://CVE-2020-8112.patch \
    file://CVE-2020-15389.patch \
    file://CVE-2020-27814-1.patch \
    file://CVE-2020-27814-2.patch \
    file://CVE-2020-27814-3.patch \
    file://CVE-2020-27814-4.patch \
    file://CVE-2020-27823.patch \
    file://CVE-2020-27824.patch \
    file://CVE-2020-27841.patch \
    file://CVE-2020-27842.patch \
    file://CVE-2020-27843.patch \
    file://CVE-2020-27845.patch \
"
SRCREV = "57096325457f96d8cd07bd3af04fe81d7a2ba788"
S = "${WORKDIR}/git"

inherit cmake

# for multilib
EXTRA_OECMAKE += "-DOPENJPEG_INSTALL_LIB_DIR=${@d.getVar('baselib').replace('/', '')}"

FILES_${PN} += "${libdir}/openjpeg*"

# This flaw is introduced by
# https://github.com/uclouvain/openjpeg/commit/4edb8c83374f52cd6a8f2c7c875e8ffacccb5fa5
# but the contents of this patch is not present in openjpeg_2.3.1
# Hence, it can be whitelisted.
# https://security-tracker.debian.org/tracker/CVE-2020-27844

CVE_CHECK_WHITELIST += "CVE-2020-27844"

# The CVE description clearly states that j2k_read_ppm_v3 function in openjpeg
# is affected due to CVE-2015-1239 but in openjpeg_2.3.1 this function is not present.
# Hence, CVE-2015-1239 does not affect openjpeg_2.3.1

CVE_CHECK_WHITELIST += "CVE-2015-1239"

SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM ?= "file://LICENSING;md5=d1cc18f512ded3bd6000f3729f31be08 \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit autotools

SRCREV ?= "089479bb24acd168613757a6f12d63caa95416b4"
SRCBRANCH ?= "master"

SRC_URI = "git://github.com/besser82/libxcrypt.git;branch=${SRCBRANCH} \
           "

PROVIDES = "virtual/crypt"

FILES_${PN} = "${libdir}/libcrypt*.so.* ${libdir}/libcrypt-*.so ${libdir}/libowcrypt*.so.* ${libdir}/libowcrypt-*.so"

S = "${WORKDIR}/git"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE} -std=gnu99"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir}"

python () {
    if not bb.data.inherits_class('nativesdk', d):
        raise bb.parse.SkipRecipe("Recipe only applies in nativesdk case for now")
}

BBCLASSEXTEND = "nativesdk"

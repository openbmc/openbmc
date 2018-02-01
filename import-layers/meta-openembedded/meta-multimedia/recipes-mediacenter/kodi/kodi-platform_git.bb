SUMMARY = "Platform support library used by libCEC and binary add-ons for Kodi"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/util/XMLUtils.cpp;beginline=2;endline=18;md5=dae8e846500e70dd8ecee55f3f018c30"

DEPENDS = "libtinyxml kodi"

PV = "16.0.0"

SRCREV = "c8188d82678fec6b784597db69a68e74ff4986b5"
SRC_URI = "git://github.com/xbmc/kodi-platform.git \
           file://0001-fix-cross-compile-badness.patch \
           file://kodi-platform-02_no-multi-lib.patch \
          "

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = " -DCMAKE_INSTALL_PREFIX_TOOLCHAIN=${STAGING_DIR_TARGET} \
                  -DCMAKE_INSTALL_LIBDIR=${libdir} \
                  -DCMAKE_INSTALL_LIBDIR_NOARCH=${libdir} \
                  -DKODI_INCLUDE_DIR=${STAGING_LIBDIR}/kodi \
                  -DKODI_INCLUDE_DIR=${STAGING_INCDIR}/kodi \
                "

do_compile_prepend() {
	sed -i -e 's:I/usr/include:I${STAGING_INCDIR}:g' \
	       -e 's:-pipe:${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} -pipe:g' \
	          ${B}/CMakeFiles/kodiplatform.dir/flags.make
	sed -i -e 's:-pipe:${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS} -pipe:g'\
	          ${B}/CMakeFiles/kodiplatform.dir/link.txt
}

RPROVIDES_${PN} += "libkodiplatform"
FILES_${PN}-dev += "${libdir}/*platform"

PNBLACKLIST[kodi-platform] ?= "Depends on blacklisted kodi - the recipe will be removed on 2017-09-01 unless the issue is fixed"

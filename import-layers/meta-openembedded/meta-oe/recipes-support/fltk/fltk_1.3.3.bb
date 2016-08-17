SUMMARY = "FLTK is a cross-platform C++ GUI toolkit"
HOMEPAGE = "http://www.fltk.org"
SECTION = "libs"
LICENSE = "LGPLv2 & FLTK"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b26344a24a941a01a5b0826e80b5ca"

DEPENDS = "alsa-lib zlib jpeg libpng libxext libxft"

SRC_URI = "http://fltk.org/pub/fltk/${PV}/${BP}-source.tar.gz \
           file://disable_test.patch \
           file://fltk-no-freetype-config.patch \
           file://fix-boolean-issue-with-jpeg9.patch \
"

SRC_URI[md5sum] = "9ccdb0d19dc104b87179bd9fd10822e3"
SRC_URI[sha256sum] = "f8398d98d7221d40e77bc7b19e761adaf2f1ef8bb0c30eceb7beb4f2273d0d97"

inherit autotools-brokensep binconfig pkgconfig lib_package

TARGET_CC_ARCH += "${LDFLAGS} -DXFT_MAJOR=2"

EXTRA_OECONF = "--enable-shared \
                --enable-threads \
                --enable-xdbe \
                --enable-xft \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl', '', d)}"

PACKAGECONFIG[opengl] = "--enable-gl,--disable-gl,virtual/libgl"
PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,libxinerama"
PACKAGECONFIG[xfixes] = "--enable-xfixes,--disable-xfixes,libxfixes"
PACKAGECONFIG[xcursor] = "--enable-xcursor,--disable-xcursor,libxcursor"

do_configure() {
    oe_runconf
}

do_install_append_class-target() {
    sed -i -e 's,${STAGING_DIR_HOST},,g' ${D}${bindir}/fltk-config
}

python populate_packages_prepend () {
    if (d.getVar('DEBIAN_NAMES', 1)):
        d.setVar('PKG_${BPN}', 'libfltk${PV}')
}

LEAD_SONAME = "libfltk.so"


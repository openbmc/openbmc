SUMMARY = "libgphoto2 allows you to access digital cameras"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=477378d78dfeeaa93826ee4ec7c643fb"

DEPENDS = "libtool jpeg virtual/libusb0 libexif zlib libxml2"

# The .fdi and .rules files were generated with:
#  libgphoto2-2.5.8/packaging/generic$ qemu-arm -s 1048576 -r 2.6.24 -L /OE/angstrom-dev/staging/armv5te-angstrom-linux-gnueabi/ .libs/print-camera-list
# They are release specific, so please regen when adding new releases

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/libgphoto2-${PV}.tar.bz2;name=libgphoto2 \
           file://40-libgphoto2.rules \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
           file://0001-configure-Filter-out-buildpaths-from-CC.patch \
"
SRC_URI[libgphoto2.sha256sum] = "4f81c34c0b812bee67afd5f144940fbcbe01a2055586a6a1fa2d0626024a545b"

inherit autotools pkgconfig gettext lib_package

EXTRA_OECONF = " --with-drivers=all udevscriptdir=${nonarch_base_libdir}/udev ac_cv_lib_ltdl_lt_dlcaller_register=yes"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gd] = ",--without-gdlib,gd"
PACKAGECONFIG[serial] = "--enable-serial,--disable-serial,lockdev"

do_configure:prepend() {
    rm -rf ${S}/libgphoto2_port/auto-m4/*
    rm -rf ${S}/auto-m4/*
}

do_configure:append() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/libgphoto2_port/po/
    cd ${S}/libgphoto2_port/
    autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths
    cd ${S}
}

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/*.rules ${D}${sysconfdir}/udev/rules.d/
}

PACKAGES =+ "libgphotoport libgphoto2-camlibs"
FILES:libgphoto2-camlibs = "${libdir}/libgphoto2*/*/*.so*"
RRECOMMENDS:${PN} = "libgphoto2-camlibs"

FILES:libgphotoport = "${libdir}/libgphoto2_port.so.*"

FILES:${PN} += "${nonarch_base_libdir}/udev/*"
FILES:${PN}-dbg += "${libdir}/*/*/.debug"
FILES:${PN}-dev += "${libdir}/*/*/*.la"
FILES:${PN}-doc += "${datadir}/libgphoto2_port/0.12.?/vcamera/README.txt"

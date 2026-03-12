SUMMARY = "libgphoto2 allows you to access digital cameras"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=477378d78dfeeaa93826ee4ec7c643fb"

DEPENDS = "libtool jpeg virtual/libusb0 libexif zlib libxml2"

# The .fdi and .rules files were generated with:
#  libgphoto2-2.5.8/packaging/generic$ qemu-arm -s 1048576 -r 2.6.24 -L /OE/angstrom-dev/staging/armv5te-angstrom-linux-gnueabi/ .libs/print-camera-list
# They are release specific, so please regen when adding new releases

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/${BP}.tar.xz;name=libgphoto2 \
           file://40-libgphoto2.rules \
           file://0001-configure-Filter-out-buildpaths-from-CC.patch \
"
SRC_URI[libgphoto2.sha256sum] = "28825f767a85544cb58f6e15028f8e53a5bb37a62148b3f1708b524781c3bef2"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/gphoto/files/libgphoto/"

inherit autotools pkgconfig gettext lib_package

EXTRA_OECONF = " --with-drivers=all udevscriptdir=${nonarch_base_libdir}/udev ac_cv_lib_ltdl_lt_dlcaller_register=yes"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gd] = ",--without-gdlib,gd"
PACKAGECONFIG[serial] = "--enable-serial,--disable-serial,lockdev"

do_install:append() {
    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/*.rules ${D}${sysconfdir}/udev/rules.d/
}

PACKAGES =+ "libgphotoport libgphoto2-camlibs"
FILES:libgphoto2-camlibs = "${libdir}/libgphoto2*/*/*.so*"
RRECOMMENDS:${PN} = "libgphoto2-camlibs"

FILES:libgphotoport = "${libdir}/libgphoto2_port.so.*"

FILES:${PN} += "${nonarch_base_libdir}/udev/*"
FILES:${PN}-doc += "${datadir}/libgphoto2_port/0.12.?/vcamera/README.txt"

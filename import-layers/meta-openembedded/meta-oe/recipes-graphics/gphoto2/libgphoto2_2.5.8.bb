SUMMARY = "libgphoto2 allows you to access digital cameras"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=0448d3676bc0de00406af227d341a4d1"

DEPENDS = "libtool jpeg virtual/libusb0 libexif zlib libxml2"

# The .fdi and .rules files were generated with:
#  libgphoto2-2.5.8/packaging/generic$ qemu-arm -s 1048576 -r 2.6.24 -L /OE/angstrom-dev/staging/armv5te-angstrom-linux-gnueabi/ .libs/print-camera-list
# They are release specific, so please regen when adding new releases

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/libgphoto2-${PV}.tar.bz2;name=libgphoto2 \
           file://10-camera-libgphoto2-device.fdi \
           file://10-camera-libgphoto2.fdi \
           file://40-libgphoto2.rules \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
           file://0002-correct-jpeg-memsrcdest-support.patch \
           file://avoid_using_sprintf.patch \
           file://0001-scripts-remove-bashisms.patch \
"

SRC_URI[libgphoto2.md5sum] = "873ab01aced49c6b92a98e515db5dcef"
SRC_URI[libgphoto2.sha256sum] = "031a262e342fae43f724afe66787947ce1fb483277dfe5a8cf1fbe92c58e27b6"

inherit autotools pkgconfig gettext lib_package

EXTRA_OECONF = " --with-drivers=all udevscriptdir=${nonarch_base_libdir}/udev ac_cv_lib_ltdl_lt_dlcaller_register=yes"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gd] = ",--without-gdlib,gd"
PACKAGECONFIG[serial] = "--enable-serial,--disable-serial,lockdev"

do_configure_append() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/libgphoto2_port/po/
    cd ${S}/libgphoto2_port/
    autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths
    cd ${S}
}

do_install_append() {
    install -d ${D}${datadir}/hal/fdi/information/20thirdparty
    install -m 0644 ${WORKDIR}/*.fdi ${D}${datadir}/hal/fdi/information/20thirdparty/

    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0755 ${WORKDIR}/*.rules ${D}${sysconfdir}/udev/rules.d/
}

PACKAGES =+ "libgphotoport libgphoto2-camlibs"
FILES_libgphoto2-camlibs = "${libdir}/libgphoto2*/*/*.so*"
RRECOMMENDS_${PN} = "libgphoto2-camlibs"

FILES_libgphotoport = "${libdir}/libgphoto2_port.so.*"

FILES_${PN} += "${nonarch_base_libdir}/udev/* ${datadir}/hal"
FILES_${PN}-dbg += "${libdir}/*/*/.debug"
FILES_${PN}-dev += "${libdir}/*/*/*.la"

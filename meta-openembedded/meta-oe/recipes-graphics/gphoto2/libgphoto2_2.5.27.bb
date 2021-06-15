SUMMARY = "libgphoto2 allows you to access digital cameras"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=0448d3676bc0de00406af227d341a4d1"

DEPENDS = "libtool jpeg virtual/libusb0 libexif zlib libxml2"

# The .fdi and .rules files were generated with:
#  libgphoto2-2.5.8/packaging/generic$ qemu-arm -s 1048576 -r 2.6.24 -L /OE/angstrom-dev/staging/armv5te-angstrom-linux-gnueabi/ .libs/print-camera-list
# They are release specific, so please regen when adding new releases

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/libgphoto2-${PV}.tar.bz2;name=libgphoto2 \
           file://40-libgphoto2.rules \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
"

SRC_URI[libgphoto2.md5sum] = "83a2f96dade72e95dffb8e5fa9628d7e"
SRC_URI[libgphoto2.sha256sum] = "f8b85478c44948a0b0b52c4d4dfda2de1d7bcb7b262c76bd1ae306d9c63240d7"

inherit autotools pkgconfig gettext lib_package

EXTRA_OECONF = " --with-drivers=all udevscriptdir=${nonarch_base_libdir}/udev ac_cv_lib_ltdl_lt_dlcaller_register=yes"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gd] = ",--without-gdlib,gd"
PACKAGECONFIG[serial] = "--enable-serial,--disable-serial,lockdev"

do_configure_append() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/libgphoto2_port/po/
    cd ${S}/libgphoto2_port/
    autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths

    # remove WORKDIR information from config to improve reproducibility
    # libgphoto2_port recheck config will set the WORKDIR info again, so dont do that
    sed -i 's/'$(echo ${WORKDIR} | sed 's_/_\\/_g')'/../g' ${B}/config.h
    sed -i 's/'$(echo ${WORKDIR} | sed 's_/_\\/_g')'/../g' ${B}/libgphoto2_port/config.status
    sed -i '/config\.status/ s/\-\-recheck//' ${B}/libgphoto2_port/Makefile
    cd ${S}
}

do_install_append() {
    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/*.rules ${D}${sysconfdir}/udev/rules.d/
}

PACKAGES =+ "libgphotoport libgphoto2-camlibs"
FILES_libgphoto2-camlibs = "${libdir}/libgphoto2*/*/*.so*"
RRECOMMENDS_${PN} = "libgphoto2-camlibs"

FILES_libgphotoport = "${libdir}/libgphoto2_port.so.*"

FILES_${PN} += "${nonarch_base_libdir}/udev/*"
FILES_${PN}-dbg += "${libdir}/*/*/.debug"
FILES_${PN}-dev += "${libdir}/*/*/*.la"
FILES_${PN}-doc += "${datadir}/libgphoto2_port/0.12.0/vcamera/README.txt"

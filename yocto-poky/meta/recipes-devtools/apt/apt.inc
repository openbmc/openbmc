SUMMARY = "Advanced front-end for dpkg"
LICENSE = "GPLv2.0+"
SECTION = "base"

SRC_URI = "http://snapshot.debian.org/archive/debian/20150805T094928Z/pool/main/a/${BPN}/${BPN}_${PV}.tar.xz \
           file://use-host.patch \
           file://makerace.patch \
           file://no-nls-dpkg.patch \
           file://fix-gcc-4.6-null-not-defined.patch \
           file://truncate-filename.patch \
           file://nodoc.patch \
           file://disable-configure-in-makefile.patch \
           file://disable-test.patch \
           "
SRC_URI[md5sum] = "6505c4297b338adb2087ce87bbc4a276"
SRC_URI[sha256sum] = "3fb1de9598363c416591d49e3c285458e095b035e6c06d5b944a54e15fc9b543"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"

inherit autotools gettext

EXTRA_AUTORECONF = "--exclude=autopoint,autoheader"

do_configure_prepend() {
    rm -rf ${S}/buildlib/config.sub
    rm -rf ${S}/buildlib/config.guess
}

FILES_${PN}-dbg += "${libdir}/apt/methods/.debug/"

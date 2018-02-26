SUMMARY = "CORBA ORB"
HOMEPAGE = "http://www.gnome.org/projects/ORBit2"
SECTION = "x11/gnome/libs"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SRC_NAME = "ORBit2"
SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"
SRC_URI = " \
    ${GNOME_MIRROR}/${SRC_NAME}/${SHRT_VER}/${SRC_NAME}-${PV}.tar.bz2 \
    file://configure-lossage.patch \
    file://pkgconfig-fix.patch \
    file://0001-linc2-src-Makefile.am-fix-build-with-glib-2.36.patch \
    file://0002-Use-AM_CPPFLAGS-instead-of-INCLUDES-and-fix-problem-.patch \
"
SRC_URI[md5sum] = "7082d317a9573ab338302243082d10d1"
SRC_URI[sha256sum] = "55c900a905482992730f575f3eef34d50bda717c197c97c08fa5a6eafd857550"

BBCLASSEXTEND = "native"

IDL_COMPILER_DEPENDS = "orbit2-native"
IDL_COMPILER_DEPENDS_class-native = " "
DEPENDS = "libidl popt ${IDL_COMPILER_DEPENDS}"

# IDL_COMPILER_DEPENDS_class-native for some reason didn't work and orbit2-native
# was still in orbit2-native DEPENDS causing circular dependency
DEPENDS_class-native = "libidl-native popt-native"
PARALLEL_MAKE = ""


FILES_${PN} += "${libdir}/orbit-2.0/*.so"
FILES_${PN}-dev += "${libdir}/orbit-2.0/*.la"
FILES_${PN}-staticdev += "${libdir}/orbit-2.0/*.a"
FILES_${PN}-dbg += "${libdir}/orbit-2.0/.debug"

S = "${WORKDIR}/${SRC_NAME}-${PV}"

LEAD_SONAME = "libORBit-2.so"

inherit autotools pkgconfig gtk-doc

EXTRA_OEMAKE = "IDL_COMPILER='${STAGING_BINDIR_NATIVE}/orbit-idl-2'"
EXTRA_OEMAKE_class-native = " "

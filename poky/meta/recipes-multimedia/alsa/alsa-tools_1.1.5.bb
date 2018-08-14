SUMMARY = "Advanced tools for certain ALSA sound card drivers"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2+"
DEPENDS = "alsa-lib ncurses glib-2.0"

LIC_FILES_CHKSUM = "file://hdsploader/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://ld10k1/COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "ftp://ftp.alsa-project.org/pub/tools/${BP}.tar.bz2 \
           file://autotools.patch \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', 'file://makefile_no_gtk.patch', d)} \
           file://gitcompile_hdajacksensetest \
           "

SRC_URI[md5sum] = "3afb92eb1b4f2edc8691498e57c3ec78"
SRC_URI[sha256sum] = "bc3c6567de835223ee7d69487b8c22fb395a2e8c613341b0c96e6a5f6a2bd534"

inherit autotools-brokensep pkgconfig

CLEANBROKEN = "1"

EXTRA_OEMAKE += "GITCOMPILE_ARGS='--host=${HOST_SYS} --build=${BUILD_SYS} --target=${TARGET_SYS} --with-libtool-sysroot=${STAGING_DIR_HOST} --prefix=${prefix}'"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk+', '', d)}"
PACKAGECONFIG[gtk+] = ",,gtk+ gtk+3,"

# configure.ac/.in doesn't exist so force copy
AUTOTOOLS_COPYACLOCAL = "1"

do_compile_prepend () {
    #Automake dir is not correctly detected in cross compilation case
    export AUTOMAKE_DIR="$(automake --print-libdir)"
    export ACLOCAL_FLAGS="--system-acdir=${ACLOCALDIR}/ ${ACLOCALEXTRAPATH}"

    cp ${WORKDIR}/gitcompile_hdajacksensetest ${S}/hdajacksensetest/gitcompile
}

FILES_${PN} += "${datadir}/ld10k1"

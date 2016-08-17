SUMMARY = "XBMC Media Center"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.GPL;md5=6eb631b6da7fdb01508a80213ffc35ff"

DEPENDS = "libusb1 libcec libplist expat yajl gperf-native libxmu fribidi mpeg2dec ffmpeg samba fontconfig curl python libass libmodplug libmicrohttpd wavpack libmms cmake-native libsdl-image libsdl-mixer virtual/egl mysql5 sqlite3 libmms faad2 libcdio libpcre boost lzo enca avahi libsamplerate0 libxinerama libxrandr libxtst bzip2 virtual/libsdl jasper zip-native zlib libtinyxml libmad"
#require recipes/egl/egl.inc


SRCREV = "82388d55dae79cbb2e486e307e23202e76a43efa"

# multiple issues
PNBLACKLIST[xbmc] ?= "/usr/include/c++/ctime:70:11: error: '::gmtime' has not been declared"

PV = "11.0+gitr${SRCPV}"
PR = "r14"
SRC_URI = "git://github.com/xbmc/xbmc.git;branch=Eden \
           file://0001-configure-don-t-run-python-distutils-to-find-STAGING.patch \
           file://0002-Revert-fixed-ios-Add-memory-barriers-to-atomic-Add-S.patch \
           file://0003-Revert-fixed-ios-Add-memory-barriers-to-cas-assembly.patch \
           file://0004-configure-cope-with-ld-is-gold-DISTRO_FEATURE.patch \
           file://configure.in-Avoid-running-code.patch \
"

inherit autotools gettext python-dir

S = "${WORKDIR}/git"

# breaks compilation
CCACHE = ""

CACHED_CONFIGUREVARS += " \
    ac_cv_path_PYTHON="${STAGING_BINDIR_NATIVE}/python-native/python" \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl', 'openglesv2', d)}"
PACKAGECONFIG[opengl] = "--enable-gl,--enable-gles,glew"
PACKAGECONFIG[openglesv2] = "--enable-gles,--enable-gl,"

EXTRA_OECONF = " \
    --disable-rpath \
    --enable-libusb \
    --enable-airplay \
    --disable-optical-drive \
    --enable-external-libraries \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', '--enable-gl', '--enable-gles', d)} \
"

FULL_OPTIMIZATION_armv7a = "-fexpensive-optimizations -fomit-frame-pointer -O4 -ffast-math"
BUILD_OPTIMIZATION = "${FULL_OPTIMIZATION}"

EXTRA_OECONF_append_armv7a = "--cpu=cortex-a8"

# for python modules
export HOST_SYS
export BUILD_SYS
export STAGING_LIBDIR
export STAGING_INCDIR
export PYTHON_DIR

do_configure() {
    sh bootstrap
    oe_runconf
}

PARALLEL_MAKE = ""

do_compile_prepend() {
    for i in $(find . -name "Makefile") ; do
        sed -i -e 's:I/usr/include:I${STAGING_INCDIR}:g' $i
    done

    for i in $(find . -name "*.mak*" -o    -name "Makefile") ; do
        sed -i -e 's:I/usr/include:I${STAGING_INCDIR}:g' -e 's:-rpath \$(libdir):-rpath ${libdir}:g' $i
    done
}

INSANE_SKIP_${PN} = "rpaths"

# on ARM architectures xbmc will use GLES which will make the regular wrapper fail, so start it directly
do_install_append_arm() {
    sed -i -e 's:Exec=xbmc:Exec=${libdir}/xbmc/xbmc.bin:g' ${D}${datadir}/applications/xbmc.desktop
}

FILES_${PN} += "${datadir}/xsessions ${datadir}/icons"
FILES_${PN}-dbg += "${libdir}/xbmc/.debug ${libdir}/xbmc/*/.debug ${libdir}/xbmc/*/*/.debug ${libdir}/xbmc/*/*/*/.debug"

# xbmc uses some kind of dlopen() method for libcec so we need to add it manually
# OpenGL builds need glxinfo, that's in mesa-demos
RRECOMMENDS_${PN}_append = " libcec \
                             python \
                             python-lang \
                             python-re \
                             python-netclient \
                             libcurl \
                             xdpyinfo \
                             ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'mesa-demos', '', d)} \
"
RRECOMMENDS_${PN}_append_libc-glibc = " glibc-charmap-ibm850 glibc-gconv-ibm850"

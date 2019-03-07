SUMMARY = "A free implementation of the OpenGL API"
DESCRIPTION = "Mesa is an open-source implementation of the OpenGL specification - \
a system for rendering interactive 3D graphics.  \
A variety of device drivers allows Mesa to be used in many different environments \
ranging from software emulation to complete hardware acceleration for modern GPUs. \
Mesa is used as part of the overall Direct Rendering Infrastructure and X.org \
environment."

HOMEPAGE = "http://mesa3d.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/license.html;md5=725f991a1cc322aa7a0cd3a2016621c4"

PE = "2"

DEPENDS = "expat makedepend-native flex-native bison-native libxml2-native zlib chrpath-replacement-native"
EXTRANATIVEPATH += "chrpath-native"
PROVIDES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'opengl', 'virtual/libgl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gles', 'virtual/libgles1 virtual/libgles2', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'virtual/egl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gbm', 'virtual/libgbm', '', d)} \
    virtual/mesa \
    "

inherit autotools pkgconfig python3native gettext distro_features_check

ANY_OF_DISTRO_FEATURES = "opengl vulkan"

PLATFORMS ??= "${@bb.utils.filter('PACKAGECONFIG', 'x11 wayland', d)} \
               ${@bb.utils.contains('PACKAGECONFIG', 'gbm', 'drm', '', d)} \
               surfaceless"

export LLVM_CONFIG = "${STAGING_BINDIR_NATIVE}/llvm-config${MESA_LLVM_RELEASE}"
export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR}/llvm${MESA_LLVM_RELEASE}/llvm-config"
export YOCTO_ALTERNATE_MULTILIB_NAME = "${base_libdir}"
EXTRA_OECONF = "--enable-shared-glapi \
                --disable-opencl \
                --enable-glx-read-only-text \
                PYTHON2=python2 \
                --with-llvm-prefix=${STAGING_LIBDIR}/llvm${MESA_LLVM_RELEASE} \
                --with-platforms='${PLATFORMS}'"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland vulkan', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl egl gles gbm dri', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11 dri3', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 vulkan', 'dri3', '', d)} \
		   "

# "gbm" requires "dri", "opengl"
PACKAGECONFIG[gbm] = "--enable-gbm,--disable-gbm"

X11_DEPS = "xorgproto virtual/libx11 libxext libxxf86vm libxdamage libxfixes"
# "x11" requires "opengl"
PACKAGECONFIG[x11] = "--enable-glx-tls,--disable-glx,${X11_DEPS}"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc"
PACKAGECONFIG[wayland] = ",,wayland-native wayland libdrm wayland-protocols"

DRIDRIVERS = "swrast"
DRIDRIVERS_append_x86 = ",radeon,r200,nouveau,i965,i915"
DRIDRIVERS_append_x86-64 = ",radeon,r200,nouveau,i965,i915"
# "dri" requires "opengl"
PACKAGECONFIG[dri] = "--enable-dri --with-dri-drivers=${DRIDRIVERS}, --disable-dri, xorgproto libdrm"
PACKAGECONFIG[dri3] = "--enable-dri3, --disable-dri3, xorgproto libxshmfence"

# Vulkan drivers need dri3 enabled
# radeon could be enabled as well but requires gallium-llvm with llvm >= 3.9
VULKAN_DRIVERS = ""
VULKAN_DRIVERS_append_x86 = ",intel"
VULKAN_DRIVERS_append_x86-64 = ",intel"
PACKAGECONFIG[vulkan] = "--with-vulkan-drivers=${VULKAN_DRIVERS}, --without-vulkan-drivers, python3-mako-native"

PACKAGECONFIG[opengl] = "--enable-opengl, --disable-opengl"

# "gles" requires "opengl"
PACKAGECONFIG[gles] = "--enable-gles1 --enable-gles2, --disable-gles1 --disable-gles2"

# "egl" requires "dri", "opengl"
PACKAGECONFIG[egl] = "--enable-egl, --disable-egl"

PACKAGECONFIG[etnaviv] = ""
PACKAGECONFIG[imx] = ""

GALLIUMDRIVERS = "swrast"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'imx', ',imx', '', d)}"
GALLIUMDRIVERS_LLVM33 = "${@bb.utils.contains('PACKAGECONFIG', 'r600', 'radeonsi,r600', '', d)}"
PACKAGECONFIG[r600] = ""
GALLIUMDRIVERS_LLVM33_ENABLED = "${@oe.utils.version_less_or_equal('MESA_LLVM_RELEASE', '3.2', False, len('${GALLIUMDRIVERS_LLVM33}') > 0, d)}"
GALLIUMDRIVERS_LLVM = "r300,svga,nouveau${@',${GALLIUMDRIVERS_LLVM33}' if ${GALLIUMDRIVERS_LLVM33_ENABLED} else ''}"
GALLIUMDRIVERS_append_x86 = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', ',${GALLIUMDRIVERS_LLVM}', '', d)}"
GALLIUMDRIVERS_append_x86-64 = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', ',${GALLIUMDRIVERS_LLVM}', '', d)}"
GALLIUMDRIVERS_append_x86 = ",virgl"
GALLIUMDRIVERS_append_x86-64 = ",virgl"
# keep --with-gallium-drivers separate, because when only one of gallium versions is enabled, other 2 were adding --without-gallium-drivers
PACKAGECONFIG[gallium]      = "--enable-texture-float --with-gallium-drivers=${GALLIUMDRIVERS}, --without-gallium-drivers"
MESA_LLVM_RELEASE ?= "6.0"
PACKAGECONFIG[gallium-llvm] = "--enable-llvm --enable-llvm-shared-libs, --disable-llvm, llvm${MESA_LLVM_RELEASE} llvm-native \
                               ${@'elfutils' if ${GALLIUMDRIVERS_LLVM33_ENABLED} else ''}"
export WANT_LLVM_RELEASE = "${MESA_LLVM_RELEASE}"
PACKAGECONFIG[xa]  = "--enable-xa, --disable-xa"

OSMESA = "${@bb.utils.contains('PACKAGECONFIG', 'gallium', 'gallium-osmesa', 'osmesa', d)}"
PACKAGECONFIG[osmesa] = "--enable-${OSMESA},--disable-${OSMESA}"

PACKAGECONFIG[unwind] = "--enable-libunwind,--disable-libunwind,libunwind"

EXTRA_OECONF_remove_libc-musl = "--enable-glx-tls"
EXTRA_OECONF_append_libc-musl = " --disable-glx-tls"
EXTRA_OECONF_append_libc-musl_x86 = " --disable-asm"

# llvmpipe is slow if compiled with -fomit-frame-pointer (e.g. -O2)
FULL_OPTIMIZATION_append = " -fno-omit-frame-pointer"

CFLAGS_append_armv5 = " -DMISSING_64BIT_ATOMICS"
EXTRA_OEMAKE += "WAYLAND_PROTOCOLS_DATADIR=${STAGING_DATADIR}/wayland-protocols"

# Remove the mesa dependency on mesa-dev, as mesa is empty
RDEPENDS_${PN}-dev = ""

# Add dependency so that GLES3 header don't need to be added manually
RDEPENDS_libgles2-mesa-dev += "libgles3-mesa-dev"

PACKAGES =+ "libegl-mesa libegl-mesa-dev \
             libosmesa libosmesa-dev \
             libgl-mesa libgl-mesa-dev \
             libglapi libglapi-dev \
             libgbm libgbm-dev \
             libgles1-mesa libgles1-mesa-dev \
             libgles2-mesa libgles2-mesa-dev \
             libgles3-mesa libgles3-mesa-dev \
             libxatracker libxatracker-dev \
             mesa-megadriver mesa-vulkan-drivers \
            "

do_install_append () {
    # Drivers never need libtool .la files
    rm -f ${D}${libdir}/dri/*.la
    rm -f ${D}${libdir}/egl/*.la
    rm -f ${D}${libdir}/gallium-pipe/*.la
    rm -f ${D}${libdir}/gbm/*.la

    chrpath --delete ${D}${libdir}/dri/*_dri.so || true

    # libwayland-egl has been moved to wayland 1.15+
    rm -f ${D}${libdir}/libwayland-egl*
    rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
    rmdir --ignore-fail-on-non-empty ${D}${libdir}/pkgconfig
}

# For the packages that make up the OpenGL interfaces, inject variables so that
# they don't get Debian-renamed (which would remove the -mesa suffix), and
# RPROVIDEs/RCONFLICTs on the generic libgl name.
python __anonymous() {
    pkgconfig = (d.getVar('PACKAGECONFIG') or "").split()
    for p in (("egl", "libegl", "libegl1"),
              ("dri", "libgl", "libgl1"),
              ("gles", "libgles1", "libglesv1-cm1"),
              ("gles", "libgles2", "libglesv2-2"),
              ("gles", "libgles3",)):
        if not p[0] in pkgconfig:
            continue
        fullp = p[1] + "-mesa"
        pkgs = " ".join(p[1:])
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)

        d.appendVar("RRECOMMENDS_" + fullp, " mesa-megadriver")

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        pkgs = p[1] + "-dev"
        d.setVar("DEBIAN_NOAUTONAME_" + fullp, "1")
        d.appendVar("RREPLACES_" + fullp, pkgs)
        d.appendVar("RPROVIDES_" + fullp, pkgs)
        d.appendVar("RCONFLICTS_" + fullp, pkgs)
}

python mesa_populate_packages() {
    pkgs = ['mesa', 'mesa-dev', 'mesa-dbg']
    for pkg in pkgs:
        d.setVar("RPROVIDES_%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))
        d.setVar("RCONFLICTS_%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))
        d.setVar("RREPLACES_%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))

    import re
    dri_drivers_root = oe.path.join(d.getVar('PKGD'), d.getVar('libdir'), "dri")
    if os.path.isdir(dri_drivers_root):
        dri_pkgs = os.listdir(dri_drivers_root)
        lib_name = d.expand("${MLPREFIX}mesa-megadriver")
        for p in dri_pkgs:
            m = re.match('^(.*)_dri\.so$', p)
            if m:
                pkg_name = " ${MLPREFIX}mesa-driver-%s" % legitimize_package_name(m.group(1))
                d.appendVar("RPROVIDES_%s" % lib_name, pkg_name)
                d.appendVar("RCONFLICTS_%s" % lib_name, pkg_name)
                d.appendVar("RREPLACES_%s" % lib_name, pkg_name)

    pipe_drivers_root = os.path.join(d.getVar('libdir'), "gallium-pipe")
    do_split_packages(d, pipe_drivers_root, '^pipe_(.*)\.so$', 'mesa-driver-pipe-%s', 'Mesa %s pipe driver', extra_depends='')
}

PACKAGESPLITFUNCS_prepend = "mesa_populate_packages "

PACKAGES_DYNAMIC += "^mesa-driver-.*"

FILES_${PN} = ""
FILES_mesa-megadriver = "${libdir}/dri/* ${sysconfdir}"
FILES_mesa-vulkan-drivers = "${libdir}/libvulkan_*.so ${datadir}/vulkan"
FILES_libegl-mesa = "${libdir}/libEGL.so.*"
FILES_libgbm = "${libdir}/libgbm.so.*"
FILES_libgles1-mesa = "${libdir}/libGLESv1*.so.*"
FILES_libgles2-mesa = "${libdir}/libGLESv2.so.*"
FILES_libgl-mesa = "${libdir}/libGL.so.*"
FILES_libglapi = "${libdir}/libglapi.so.*"
FILES_libosmesa = "${libdir}/libOSMesa.so.*"
FILES_libxatracker = "${libdir}/libxatracker.so.*"

FILES_${PN}-dev = "${libdir}/pkgconfig/dri.pc ${includedir}/vulkan"
FILES_libegl-mesa-dev = "${libdir}/libEGL.* ${includedir}/EGL ${includedir}/KHR ${libdir}/pkgconfig/egl.pc"
FILES_libgbm-dev = "${libdir}/libgbm.* ${libdir}/pkgconfig/gbm.pc ${includedir}/gbm.h"
FILES_libgl-mesa-dev = "${libdir}/libGL.* ${includedir}/GL ${libdir}/pkgconfig/gl.pc"
FILES_libglapi-dev = "${libdir}/libglapi.*"
FILES_libgles1-mesa-dev = "${libdir}/libGLESv1*.* ${includedir}/GLES ${libdir}/pkgconfig/glesv1*.pc"
FILES_libgles2-mesa-dev = "${libdir}/libGLESv2.* ${includedir}/GLES2 ${libdir}/pkgconfig/glesv2.pc"
FILES_libgles3-mesa-dev = "${includedir}/GLES3"
FILES_libosmesa-dev = "${libdir}/libOSMesa.* ${includedir}/GL/osmesa.h ${libdir}/pkgconfig/osmesa.pc"
FILES_libxatracker-dev = "${libdir}/libxatracker.so ${libdir}/libxatracker.la \
                          ${includedir}/xa_tracker.h ${includedir}/xa_composite.h ${includedir}/xa_context.h \
                          ${libdir}/pkgconfig/xatracker.pc"

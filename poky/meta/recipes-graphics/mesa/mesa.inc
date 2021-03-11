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
LIC_FILES_CHKSUM = "file://docs/license.html;md5=c1843d93c460bbf778d6037ce324f9f7"

PE = "2"

DEPENDS = "expat makedepend-native flex-native bison-native libxml2-native zlib chrpath-replacement-native python3-mako-native gettext-native"
EXTRANATIVEPATH += "chrpath-native"
PROVIDES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'opengl', 'virtual/libgl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gles', 'virtual/libgles1 virtual/libgles2', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'virtual/egl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gbm', 'virtual/libgbm', '', d)} \
    virtual/mesa \
    "

inherit meson pkgconfig python3native gettext features_check

BBCLASSEXTEND = "native nativesdk"

ANY_OF_DISTRO_FEATURES_class-target = "opengl vulkan"

PLATFORMS ??= "${@bb.utils.filter('PACKAGECONFIG', 'x11 wayland', d)} \
               ${@bb.utils.contains('PACKAGECONFIG', 'gbm', 'drm', '', d)} \
               surfaceless"

export YOCTO_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR}/llvm${MESA_LLVM_RELEASE}/llvm-config"
export YOCTO_ALTERNATE_MULTILIB_NAME = "${base_libdir}"
export LLVM_CONFIG = "${STAGING_BINDIR_NATIVE}/llvm-config${MESA_LLVM_RELEASE}"
export WANT_LLVM_RELEASE = "${MESA_LLVM_RELEASE}"

MESA_LLVM_RELEASE ?= "${LLVMVERSION}"

# set the MESA_BUILD_TYPE to either 'release' (default) or 'debug'
# by default the upstream mesa sources build a debug release
# here we assume the user will want a release build by default
MESA_BUILD_TYPE ?= "release"
def check_buildtype(d):
    _buildtype = d.getVar('MESA_BUILD_TYPE')
    if _buildtype not in ['release', 'debug']:
        bb.fatal("unknown build type (%s), please set MESA_BUILD_TYPE to either 'release' or 'debug'" % _buildtype)
    if _buildtype == 'debug':
        return 'debugoptimized'
    return 'plain'
MESON_BUILDTYPE = "${@check_buildtype(d)}"

EXTRA_OEMESON = " \
    -Dshared-glapi=true \
    -Dgallium-opencl=disabled \
    -Dglx-read-only-text=true \
    -Dplatforms='${@",".join("${PLATFORMS}".split())}' \
"

PACKAGECONFIG_class-target ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland vulkan', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl egl gles gbm dri gallium virgl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11 dri3', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 vulkan', 'dri3', '', d)} \
                   elf-tls \
		   "
PACKAGECONFIG_class-native ?= "gbm dri egl opengl elf-tls"
PACKAGECONFIG_class-nativesdk ?= "gbm dri egl opengl elf-tls"

PACKAGECONFIG_remove_libc-musl = "elf-tls"

# "gbm" requires "dri", "opengl"
PACKAGECONFIG[gbm] = "-Dgbm=true,-Dgbm=false"

X11_DEPS = "xorgproto virtual/libx11 libxext libxxf86vm libxdamage libxfixes xrandr"
# "x11" requires "opengl"
PACKAGECONFIG[x11] = ",-Dglx=disabled,${X11_DEPS}"
PACKAGECONFIG[elf-tls] = "-Delf-tls=true, -Delf-tls=false"
PACKAGECONFIG[xvmc] = "-Dgallium-xvmc=true,-Dgallium-xvmc=false,libxvmc"
PACKAGECONFIG[wayland] = ",,wayland-native wayland libdrm wayland-protocols"

DRIDRIVERS_class-native = "swrast"
DRIDRIVERS_class-nativesdk = "swrast"
DRIDRIVERS_append_x86_class-target = ",r100,r200,nouveau,i965,i915"
DRIDRIVERS_append_x86-64_class-target = ",r100,r200,nouveau,i965,i915"
# "dri" requires "opengl"
PACKAGECONFIG[dri] = "-Ddri=true -Ddri-drivers=${DRIDRIVERS}, -Ddri=false -Ddri-drivers='', xorgproto libdrm"
PACKAGECONFIG[dri3] = "-Ddri3=true, -Ddri3=false, xorgproto libxshmfence"

# Vulkan drivers need dri3 enabled
# radeon could be enabled as well but requires gallium-llvm with llvm >= 3.9
VULKAN_DRIVERS = ""
VULKAN_DRIVERS_append_x86_class-target = ",intel"
VULKAN_DRIVERS_append_x86-64_class-target = ",intel"
PACKAGECONFIG[vulkan] = "-Dvulkan-drivers=${VULKAN_DRIVERS}, -Dvulkan-drivers='',"

PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=false"

# "gles" requires "opengl"
PACKAGECONFIG[gles] = "-Dgles1=true -Dgles2=true, -Dgles1=false -Dgles2=false"

# "egl" requires "dri", "opengl"
PACKAGECONFIG[egl] = "-Degl=true, -Degl=false"

PACKAGECONFIG[etnaviv] = ""
PACKAGECONFIG[freedreno] = ""
PACKAGECONFIG[kmsro] = ""
PACKAGECONFIG[vc4] = ""
PACKAGECONFIG[v3d] = ""

GALLIUMDRIVERS = "swrast"
# gallium swrast was found to crash Xorg on startup in x32 qemu
GALLIUMDRIVERS_x86-x32 = ""

GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'kmsro', ',kmsro', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'vc4', ',vc4', '', d)}"
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'v3d', ',v3d', '', d)}"

# radeonsi requires LLVM
GALLIUMDRIVERS_LLVM33 = "${@bb.utils.contains('PACKAGECONFIG', 'r600', ',radeonsi', '', d)}"
GALLIUMDRIVERS_LLVM33_ENABLED = "${@oe.utils.version_less_or_equal('MESA_LLVM_RELEASE', '3.2', False, len('${GALLIUMDRIVERS_LLVM33}') > 0, d)}"
GALLIUMDRIVERS_LLVM = "r300,svga,nouveau${@',${GALLIUMDRIVERS_LLVM33}' if ${GALLIUMDRIVERS_LLVM33_ENABLED} else ''}"

PACKAGECONFIG[r600] = ""
PACKAGECONFIG[virgl] = ""

GALLIUMDRIVERS_append = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', ',${GALLIUMDRIVERS_LLVM}', '', d)}"
GALLIUMDRIVERS_append = "${@bb.utils.contains('PACKAGECONFIG', 'r600', ',r600', '', d)}"
GALLIUMDRIVERS_append = "${@bb.utils.contains('PACKAGECONFIG', 'virgl', ',virgl', '', d)}"

PACKAGECONFIG[gallium] = "-Dgallium-drivers=${GALLIUMDRIVERS}, -Dgallium-drivers=''"
PACKAGECONFIG[gallium-llvm] = "-Dllvm=true -Dshared-llvm=true, -Dllvm=false, llvm${MESA_LLVM_RELEASE} llvm-native \
                               ${@'elfutils' if ${GALLIUMDRIVERS_LLVM33_ENABLED} else ''}"
PACKAGECONFIG[xa]  = "-Dgallium-xa=true, -Dgallium-xa=false"

PACKAGECONFIG[lima] = ""
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'lima', ',lima', '', d)}"

PACKAGECONFIG[panfrost] = ""
GALLIUMDRIVERS_append ="${@bb.utils.contains('PACKAGECONFIG', 'panfrost', ',panfrost', '', d)}"

OSMESA = "${@bb.utils.contains('PACKAGECONFIG', 'gallium', 'gallium', 'classic', d)}"
PACKAGECONFIG[osmesa] = "-Dosmesa=${OSMESA},-Dosmesa=none"

PACKAGECONFIG[unwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"

# mesa tries to run cross-built gen_matypes on build machine to get struct size information
EXTRA_OEMESON_append = " -Dasm=false"

# llvmpipe is slow if compiled with -fomit-frame-pointer (e.g. -O2)
FULL_OPTIMIZATION_append = " -fno-omit-frame-pointer"

CFLAGS_append_armv5 = " -DMISSING_64BIT_ATOMICS"
CFLAGS_append_armv6 = " -DMISSING_64BIT_ATOMICS"

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

    # it was packaged in libdricore9.1.3-1 and preventing upgrades when debian.bbclass was used 
    chrpath --delete ${D}${libdir}/dri/*_dri.so || true

    # libwayland-egl has been moved to wayland 1.15+
    rm -f ${D}${libdir}/libwayland-egl*
    rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
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
            m = re.match(r'^(.*)_dri\.so$', p)
            if m:
                pkg_name = " ${MLPREFIX}mesa-driver-%s" % legitimize_package_name(m.group(1))
                d.appendVar("RPROVIDES_%s" % lib_name, pkg_name)
                d.appendVar("RCONFLICTS_%s" % lib_name, pkg_name)
                d.appendVar("RREPLACES_%s" % lib_name, pkg_name)

    pipe_drivers_root = os.path.join(d.getVar('libdir'), "gallium-pipe")
    do_split_packages(d, pipe_drivers_root, r'^pipe_(.*)\.so$', 'mesa-driver-pipe-%s', 'Mesa %s pipe driver', extra_depends='')
}

PACKAGESPLITFUNCS_prepend = "mesa_populate_packages "

PACKAGES_DYNAMIC += "^mesa-driver-.*"

FILES_mesa-megadriver = "${libdir}/dri/* ${datadir}/drirc.d/00-mesa-defaults.conf"
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

# Fix upgrade path from mesa to mesa-megadriver
RREPLACES_mesa-megadriver = "mesa"
RCONFLICTS_mesa-megadriver = "mesa"
RPROVIDES_mesa-megadriver = "mesa"

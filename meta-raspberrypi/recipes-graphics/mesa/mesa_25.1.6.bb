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
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=ffe678546d4337b732cfd12262e6af11"

PE = "2"

SRC_URI = "https://archive.mesa3d.org/mesa-${PV}.tar.xz \
           file://0001-meson-misdetects-64bit-atomics-on-mips-clang.patch \
           file://0001-freedreno-don-t-encode-build-path-into-binaries.patch \
           file://0001-dont-build-clover-frontend.patch \
           file://0001-fix-FTBFS-clc-switch-to-new-non-owned-TargetOptions-.patch \
"

SRC_URI[sha256sum] = "9f2b69eb39d2d8717d30a9868fdda3e0c0d3708ba32778bbac8ddb044538ce84"
PV = "25.1.6"

UPSTREAM_CHECK_GITTAGREGEX = "mesa-(?P<pver>\d+(\.\d+)+)"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install:append() {
  # sed can't find EGL/eglplatform.h as it doesn't get installed when glvnd enabled.
  # So, check if EGL/eglplatform.h exists before running sed.
  if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)} && [ -f ${D}${includedir}/EGL/eglplatform.h ]; then
      sed -i -e 's/^#elif defined(__unix__) && defined(EGL_NO_X11)$/#elif defined(__unix__) \&\& defined(EGL_NO_X11) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
  fi
  # These are ICDs, apps are not supposed to link against them                                                                                                              
  if ${@bb.utils.contains('PACKAGECONFIG', 'glvnd', 'true', 'false', d)} ; then                                                                                             
      rm -f ${D}${libdir}/libEGL_mesa.so ${D}${libdir}/libGLX_mesa.so                                                                                                       
  fi
}

DEPENDS = "expat makedepend-native flex-native bison-native libxml2-native zlib chrpath-replacement-native python3-mako-native gettext-native python3-pyyaml-native"
EXTRANATIVEPATH += "chrpath-native"
GLPROVIDES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'opengl', 'virtual/libgl', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gles', 'virtual/libgles1 virtual/libgles2 virtual/libgles3', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'virtual/egl', '', d)} \
"
PROVIDES = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'glvnd', '', d.getVar('GLPROVIDES'), d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gbm', 'virtual/libgbm', '', d)} \
    virtual/mesa \
    "

inherit meson pkgconfig python3native gettext features_check rust

BBCLASSEXTEND = "native nativesdk"

ANY_OF_DISTRO_FEATURES = "opengl vulkan"

PLATFORMS ??= "${@bb.utils.filter('PACKAGECONFIG', 'x11 wayland', d)}"

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
    -Dglx-read-only-text=true \
    -Dplatforms='${@",".join("${PLATFORMS}".split())}' \
"

def strip_comma(s):
    return s.strip(',')

PACKAGECONFIG = " \
	gallium \
	video-codecs \
	${@bb.utils.filter('DISTRO_FEATURES', 'x11 vulkan wayland glvnd', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl egl gles gbm virgl', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'vulkan', 'zink', '', d)} \
"

# skip all Rust dependencies if we are not building OpenCL"
INHIBIT_DEFAULT_RUST_DEPS = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', '', '1', d)}"

PACKAGECONFIG:append:x86 = " libclc gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:x86-64 = " libclc gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:i686 = " libclc gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:class-native = " libclc gallium-llvm amd nouveau svga"

# "gbm" requires "opengl"
PACKAGECONFIG[gbm] = "-Dgbm=enabled,-Dgbm=disabled"

X11_DEPS = "xorgproto virtual/libx11 libxext libxxf86vm libxdamage libxfixes xrandr xorgproto libxshmfence"
# "x11" requires "opengl"
PACKAGECONFIG[x11] = ",-Dglx=disabled,${X11_DEPS}"
PACKAGECONFIG[wayland] = ",,wayland-native wayland libdrm wayland-protocols"

VULKAN_DRIVERS_AMD = "${@bb.utils.contains('PACKAGECONFIG', 'amd', ',amd', '', d)}"
VULKAN_DRIVERS_ASAHI = "${@bb.utils.contains('PACKAGECONFIG', 'asahi libclc opencl', ',asahi', '', d)}"
VULKAN_DRIVERS_INTEL = "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc', ',intel', '', d)}"
VULKAN_DRIVERS_SWRAST = ",swrast"
# Crashes on x32
VULKAN_DRIVERS_SWRAST:x86-x32 = ""
VULKAN_DRIVERS_LLVM = "${VULKAN_DRIVERS_SWRAST}${VULKAN_DRIVERS_AMD}${VULKAN_DRIVERS_ASAHI}${VULKAN_DRIVERS_INTEL}"

VULKAN_DRIVERS = ""
VULKAN_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
VULKAN_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'broadcom', ',broadcom', '', d)}"
VULKAN_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', '${VULKAN_DRIVERS_LLVM}', '', d)}"
VULKAN_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'imagination', ',imagination-experimental', '', d)}"
VULKAN_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'panfrost', ',panfrost', '', d)}"
PACKAGECONFIG[vulkan] = "-Dvulkan-drivers=${@strip_comma('${VULKAN_DRIVERS}')}, -Dvulkan-drivers='',glslang-native vulkan-loader vulkan-headers"

# mesa development and testing tools support, per driver
TOOLS = ""
TOOLS_DEPS = ""
TOOLS:append = "${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
TOOLS:append = "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
TOOLS:append = "${@bb.utils.contains('PACKAGECONFIG', 'lima', ',lima', '', d)}"
TOOLS:append = "${@bb.utils.contains('PACKAGECONFIG', 'panfrost', ',panfrost', '', d)}"
TOOLS:append = "${@bb.utils.contains('PACKAGECONFIG', 'imagination', ',imagination', '', d)}"

# dependencies for tools.
TOOLS_DEPS:append = "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ' ncurses libxml2 ', '', d)}"

# the fdperf tool requires libconfig (a part of meta-oe) so it needs special
# treatment in addition to the usual 'freedreno tools'.
PACKAGECONFIG[freedreno-fdperf] = ",,libconfig"

PACKAGECONFIG[tools] = "-Dtools=${@strip_comma('${TOOLS}')}, -Dtools='', ${TOOLS_DEPS}"

PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=false"
PACKAGECONFIG[glvnd] = "-Dglvnd=enabled, -Dglvnd=disabled, libglvnd"

# "gles" requires "opengl"
PACKAGECONFIG[gles] = "-Dgles1=enabled -Dgles2=enabled, -Dgles1=disabled -Dgles2=disabled"

# "egl" requires "opengl"
PACKAGECONFIG[egl] = "-Degl=enabled, -Degl=disabled"

# "opencl" also requires libclc and gallium-llvm to be present in PKGCONFIG!
# Be sure to enable them both for the target and for the native build.
PACKAGECONFIG[opencl] = "-Dgallium-rusticl=true, -Dgallium-rusticl=false, bindgen-cli-native"

PACKAGECONFIG[broadcom] = ""
PACKAGECONFIG[etnaviv] = ",,python3-pycparser-native"
PACKAGECONFIG[freedreno] = ""
PACKAGECONFIG[vc4] = ""
PACKAGECONFIG[v3d] = ""
PACKAGECONFIG[zink] = ""

GALLIUMDRIVERS = "softpipe"
# gallium swrast was found to crash Xorg on startup in x32 qemu
GALLIUMDRIVERS:x86-x32 = ""

GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'vc4', ',vc4', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'v3d', ',v3d', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'zink', ',zink', '', d)}"

GALLIUMDRIVERS_ASAHI = "${@bb.utils.contains('PACKAGECONFIG', 'asahi libclc opencl', ',asahi', '', d)}"
GALLIUMDRIVERS_AMD = "${@bb.utils.contains('PACKAGECONFIG', 'amd', ',r300', '', d)}"
GALLIUMDRIVERS_IRIS = "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc', ',iris', '', d)}"
GALLIUMDRIVERS_NOUVEAU = "${@bb.utils.contains('PACKAGECONFIG', 'nouveau', ',nouveau', '', d)}"
GALLIUMDRIVERS_RADEONSI = "${@bb.utils.contains('PACKAGECONFIG', 'amd', ',radeonsi', '', d)}"
GALLIUMDRIVERS_LLVMPIPE = ",llvmpipe"
# llvmpipe crashes on x32
GALLIUMDRIVERS_LLVMPIPE:x86-x32 = ""
GALLIUMDRIVERS_SVGA = "${@bb.utils.contains('PACKAGECONFIG', 'svga', ',svga', '', d)}"
GALLIUMDRIVERS_LLVM = "${GALLIUMDRIVERS_LLVMPIPE}${GALLIUMDRIVERS_AMD}${GALLIUMDRIVERS_ASAHI}${GALLIUMDRIVERS_IRIS}${GALLIUMDRIVERS_NOUVEAU}${GALLIUMDRIVERS_RADEONSI}${GALLIUMDRIVERS_SVGA}"

PACKAGECONFIG[amd] = ""
PACKAGECONFIG[nouveau] = ""
PACKAGECONFIG[svga] = ""
PACKAGECONFIG[virgl] = ""

GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', '${GALLIUMDRIVERS_LLVM}', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'amd', ',r600', '', d)}"
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'virgl', ',virgl', '', d)}"

MESA_CLC = "system"
MESA_CLC:class-native = "enabled"
INSTALL_MESA_CLC = "false"
INSTALL_MESA_CLC:class-native = "true"
MESA_NATIVE = "mesa-native"
MESA_NATIVE:class-native = ""

PACKAGECONFIG[gallium] = "-Dgallium-drivers=${@strip_comma('${GALLIUMDRIVERS}')}, -Dgallium-drivers='', libdrm"
PACKAGECONFIG[gallium-llvm] = "-Dllvm=enabled -Dshared-llvm=enabled, -Dllvm=disabled, llvm llvm-native elfutils"
PACKAGECONFIG[libclc] = "-Dmesa-clc=${MESA_CLC} -Dinstall-mesa-clc=${INSTALL_MESA_CLC} -Dmesa-clc-bundle-headers=enabled,,libclc spirv-tools spirv-llvm-translator ${MESA_NATIVE}"
PACKAGECONFIG[va] = "-Dgallium-va=enabled,-Dgallium-va=disabled,libva-initial"
PACKAGECONFIG[vdpau] = "-Dgallium-vdpau=enabled,-Dgallium-vdpau=disabled,libvdpau"

PACKAGECONFIG[imagination] = "-Dimagination-srv=true,-Dimagination-srv=false"

PACKAGECONFIG[asahi] = ""

PACKAGECONFIG[intel] = ""
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'intel', ',i915,crocus', '', d)}"

PACKAGECONFIG[lima] = ""
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'lima', ',lima', '', d)}"

PACKAGECONFIG[panfrost] = ""
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'panfrost', ',panfrost', '', d)}"

PACKAGECONFIG[tegra] = ""
GALLIUMDRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'tegra', ',tegra,nouveau', '', d)}"

PACKAGECONFIG[vulkan-beta] = "-Dvulkan-beta=true,-Dvulkan-beta=false"

PACKAGECONFIG[perfetto] = "-Dperfetto=true,-Dperfetto=false,libperfetto"

PACKAGECONFIG[unwind] = "-Dlibunwind=enabled,-Dlibunwind=disabled,libunwind"

PACKAGECONFIG[lmsensors] = "-Dlmsensors=enabled,-Dlmsensors=disabled,lmsensors"

VIDEO_CODECS ?= "${@bb.utils.contains('LICENSE_FLAGS_ACCEPTED', 'commercial', 'all', 'all_free', d)}"
PACKAGECONFIG[video-codecs] = "-Dvideo-codecs=${VIDEO_CODECS}, -Dvideo-codecs=''"

PACKAGECONFIG[teflon] = "-Dteflon=true, -Dteflon=false"

# llvmpipe is slow if compiled with -fomit-frame-pointer (e.g. -O2)
FULL_OPTIMIZATION:append = " -fno-omit-frame-pointer"

CFLAGS:append:armv5 = " -DMISSING_64BIT_ATOMICS"
CFLAGS:append:armv6 = " -DMISSING_64BIT_ATOMICS"

# Remove the mesa dependency on mesa-dev, as mesa is empty
DEV_PKG_DEPENDENCY = ""

# GLES2 and GLES3 implementations are packaged in a single library in libgles2-mesa.
# Add a dependency so the GLES3 dev package is associated with its implementation.
RPROVIDES:libgles2-mesa += "libgles3-mesa"
RPROVIDES:libgles2-mesa-dev += "libgles3-mesa-dev"

RDEPENDS:libopencl-mesa += "${@bb.utils.contains('PACKAGECONFIG', 'opencl', 'libclc spirv-tools spirv-llvm-translator', '', d)}"

PACKAGES =+ "libegl-mesa libegl-mesa-dev \
             libgallium \
             libgl-mesa libgl-mesa-dev \
             libglx-mesa libglx-mesa-dev \
             libglapi libglapi-dev \
             libgbm libgbm-dev \
             libgles1-mesa libgles1-mesa-dev \
             libgles2-mesa libgles2-mesa-dev \
             libopencl-mesa \
             libteflon \
             mesa-megadriver mesa-vulkan-drivers \
             mesa-vdpau-drivers mesa-tools \
            "

do_install:append () {
    # libwayland-egl has been moved to wayland 1.15+
    rm -f ${D}${libdir}/libwayland-egl*
    rm -f ${D}${libdir}/pkgconfig/wayland-egl.pc
}

# For the packages that make up the OpenGL interfaces, inject variables so that
# they don't get Debian-renamed (which would remove the -mesa suffix), and
# RPROVIDEs/RCONFLICTs on the generic libgl name.
python __anonymous() {
    pkgconfig = (d.getVar('PACKAGECONFIG') or "").split()
    mlprefix = d.getVar("MLPREFIX")
    suffix = ""
    if "-native" in d.getVar("PN"):
        suffix = "-native"

    for p in ("libegl", "libgl", "libglx", "libgles1", "libgles2", "libgles3", "libopencl"):
        fullp = mlprefix + p + "-mesa" + suffix
        d.appendVar("RRECOMMENDS:" + fullp, " ${MLPREFIX}mesa-megadriver" + suffix)

    d.setVar("DEBIAN_NOAUTONAME:%slibopencl-mesa%s" % (mlprefix, suffix), "1")

    if 'glvnd' in pkgconfig:
        for p in ("libegl", "libglx"):
            fullp = mlprefix + p + "-mesa" + suffix
            d.appendVar("RPROVIDES:" + fullp, ' virtual-%s-icd' % p)
    else:
        for p in (("egl", "libegl", "libegl1"),
                  ("opengl", "libgl", "libgl1"),
                  ("gles", "libgles1", "libglesv1-cm1"),
                  ("gles", "libgles2", "libglesv2-2", "libgles3")):
            if not p[0] in pkgconfig:
                continue
            fullp = mlprefix + p[1] + "-mesa" + suffix
            pkgs = " " + " ".join(mlprefix + x + suffix for x in p[1:])
            d.setVar("DEBIAN_NOAUTONAME:" + fullp, "1")
            d.appendVar("RREPLACES:" + fullp, pkgs)
            d.appendVar("RPROVIDES:" + fullp, pkgs)
            d.appendVar("RCONFLICTS:" + fullp, pkgs)

            # For -dev, the first element is both the Debian and original name
            fullp = mlprefix + p[1] + "-mesa-dev" + suffix
            pkgs = " " + mlprefix + p[1] + "-dev" + suffix
            d.setVar("DEBIAN_NOAUTONAME:" + fullp, "1")
            d.appendVar("RREPLACES:" + fullp, pkgs)
            d.appendVar("RPROVIDES:" + fullp, pkgs)
            d.appendVar("RCONFLICTS:" + fullp, pkgs)
}

python mesa_populate_packages() {
    pkgs = ['mesa', 'mesa-dev', 'mesa-dbg']
    for pkg in pkgs:
        d.setVar("RPROVIDES:%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))
        d.setVar("RCONFLICTS:%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))
        d.setVar("RREPLACES:%s" % pkg, pkg.replace("mesa", "mesa-dri", 1))

    import re
    dri_drivers_root = oe.path.join(d.getVar('PKGD'), d.getVar('libdir'), "dri")
    if os.path.isdir(dri_drivers_root):
        dri_pkgs = sorted(os.listdir(dri_drivers_root))
        lib_name = d.expand("${MLPREFIX}mesa-megadriver")
        for p in dri_pkgs:
            m = re.match(r'^(.*)_dri\.so$', p)
            if m:
                pkg_name = " ${MLPREFIX}mesa-driver-%s" % legitimize_package_name(m.group(1))
                d.appendVar("RPROVIDES:%s" % lib_name, pkg_name)
                d.appendVar("RCONFLICTS:%s" % lib_name, pkg_name)
                d.appendVar("RREPLACES:%s" % lib_name, pkg_name)
}

PACKAGESPLITFUNCS =+ "mesa_populate_packages"

PACKAGES_DYNAMIC += "^mesa-driver-.*"
PACKAGES_DYNAMIC:class-native = "^mesa-driver-.*-native"

FILES:mesa-megadriver = "${libdir}/dri/* ${datadir}/drirc.d"
FILES:mesa-vulkan-drivers = "${libdir}/libvulkan_*.so ${libdir}/libpowervr_rogue.so ${datadir}/vulkan"
FILES:${PN}-vdpau-drivers = "${libdir}/vdpau/*.so.*"
FILES:libegl-mesa = "${libdir}/libEGL*.so.* ${datadir}/glvnd/egl_vendor.d"
FILES:libgbm = "${libdir}/libgbm.so.* ${libdir}/gbm/*_gbm.so"
FILES:libgallium = "${libdir}/libgallium-*.so"
FILES:libgles1-mesa = "${libdir}/libGLESv1*.so.*"
FILES:libgles2-mesa = "${libdir}/libGLESv2.so.*"
FILES:libgl-mesa = "${libdir}/libGL.so.*"
FILES:libglx-mesa = "${libdir}/libGLX*.so.*"
FILES:libopencl-mesa = "${libdir}/lib*OpenCL.so* ${sysconfdir}/OpenCL/vendors/*.icd"
FILES:libglapi = "${libdir}/libglapi.so.*"

FILES:${PN}-dev = "${libdir}/pkgconfig/dri.pc ${includedir}/GL/internal/dri_interface.h ${includedir}/vulkan ${libdir}/vdpau/*.so"
FILES:libegl-mesa-dev = "${libdir}/libEGL*.* ${includedir}/EGL ${includedir}/KHR ${libdir}/pkgconfig/egl.pc"
FILES:libgbm-dev = "${libdir}/libgbm.* ${libdir}/pkgconfig/gbm.pc ${includedir}/gbm.h ${includedir}/gbm_backend_abi.h"
FILES:libgl-mesa-dev = "${libdir}/libGL.* ${includedir}/GL/*.h ${libdir}/pkgconfig/gl.pc"
FILES:libglapi-dev = "${libdir}/libglapi.*"
FILES:libgles1-mesa-dev = "${libdir}/libGLESv1*.* ${includedir}/GLES ${libdir}/pkgconfig/glesv1*.pc"
FILES:libgles2-mesa-dev = "${libdir}/libGLESv2.* ${includedir}/GLES2 ${includedir}/GLES3 ${libdir}/pkgconfig/glesv2.pc"
FILES:libteflon = "${libdir}/libteflon.so"
# catch all to get all the tools and data
FILES:${PN}-tools = "${bindir} ${datadir}"
ALLOW_EMPTY:${PN}-tools = "1"

# All DRI drivers are symlinks to libdril_dri.so
INSANE_SKIP:${PN}-megadriver += "dev-so"

# OpenCL ICDs package also ship correspondig .so files, there is no -dev package
INSANE_SKIP:libopencl-mesa += "dev-so"

# Fix upgrade path from mesa to mesa-megadriver
RREPLACES:mesa-megadriver = "mesa"
RCONFLICTS:mesa-megadriver = "mesa"
RPROVIDES:mesa-megadriver = "mesa"

# Use this newer version only for rpi MACHINEs
COMPATIBLE_MACHINE = "^rpi$"
# This version doesn't have kmsro and dri3 added by 
# recipes-graphics/mesa/mesa_%.bbappend
# already removed in master branch with:
# https://github.com/agherzan/meta-raspberrypi/pull/1456
# https://github.com/agherzan/meta-raspberrypi/pull/1472
PACKAGECONFIG:remove:rpi = "kmsro dri3"

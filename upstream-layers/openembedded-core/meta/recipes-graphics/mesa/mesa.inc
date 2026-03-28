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
"

SRC_URI[sha256sum] = "ddb7443d328e89aa45b4b6b80f077bf937f099daeca8ba48cabe32aab769e134"
PV = "26.0.3"

UPSTREAM_CHECK_GITTAGREGEX = "mesa-(?P<pver>\d+(\.\d+)+)"

S = "${UNPACKDIR}/mesa-${PV}"

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

# All dependencies **MUST** be -native. If not, then add a PACKAGECONFIG for it.
DEPENDS = "makedepend-native flex-native bison-native chrpath-replacement-native python3-mako-native gettext-native python3-pyyaml-native"
EXTRANATIVEPATH += "chrpath-native"

inherit meson pkgconfig python3native gettext features_check rust

ANY_OF_DISTRO_FEATURES = "opencl opengl vulkan"

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
    -Dvulkan-manifest-per-architecture=false \
"

def strip_comma(s):
    return s.strip(',')

# skip all Rust dependencies if we are not building OpenCL"
INHIBIT_DEFAULT_RUST_DEPS = "${@bb.utils.contains('PACKAGECONFIG', 'opencl', '', '1', d)}"

# "egl" requires "opengl"
PACKAGECONFIG[egl] = "-Degl=enabled, -Degl=disabled"

# "gbm" requires "opengl"
PACKAGECONFIG[gbm] = "-Dgbm=enabled,-Dgbm=disabled"

# "gles" requires "opengl"
PACKAGECONFIG[gles] = "-Dgles1=enabled -Dgles2=enabled, -Dgles1=disabled -Dgles2=disabled"

PACKAGECONFIG[glvnd] = "-Dglvnd=enabled, -Dglvnd=disabled, libglvnd"

PACKAGECONFIG[opengl] = "-Dopengl=true, -Dopengl=false"

# "opencl" also requires libclc and gallium-llvm to be present in PACKAGECONFIG!
# Be sure to enable them both for the target and for the native build.
PACKAGECONFIG[opencl] = "-Dgallium-rusticl=true -Dmesa-clc-bundle-headers=enabled, -Dgallium-rusticl=false, bindgen-cli-native clang libclc spirv-tools spirv-llvm-translator"

X11_DEPS = "xorgproto virtual/libx11 libxext libxxf86vm libxdamage libxfixes xrandr xorgproto libxshmfence"
# "x11" requires "opengl"
PACKAGECONFIG[x11] = ",-Dglx=disabled,${X11_DEPS}"
PACKAGECONFIG[wayland] = ",,wayland-native wayland libdrm wayland-protocols"

# Entries for GPU vendors.
# Some of the drivers might have extra dependencies (libclc, gallium-llvm).
# Check them in the individual driver settings in VULKAN_DRIVERS and
# GALLIUMDRIVERS.
PACKAGECONFIG[amd] = ""
PACKAGECONFIG[asahi] = ""
PACKAGECONFIG[broadcom] = ""
PACKAGECONFIG[etnaviv] = ",,python3-pycparser-native"
PACKAGECONFIG[ethosu] = ""
PACKAGECONFIG[freedreno] = ""
PACKAGECONFIG[imagination] = "-Dimagination-srv=true,-Dimagination-srv=false"
PACKAGECONFIG[intel] = ""
PACKAGECONFIG[lima] = ""
PACKAGECONFIG[nouveau] = ""
PACKAGECONFIG[panfrost] = ""
PACKAGECONFIG[rocket] = ""
PACKAGECONFIG[svga] = ""
PACKAGECONFIG[tegra] = ""
PACKAGECONFIG[v3d] = ""
PACKAGECONFIG[vc4] = ""
PACKAGECONFIG[virtio] = ""
PACKAGECONFIG[zink] = ""


VULKAN_DRIVERS_SWRAST = ",swrast"
# Crashes on x32
VULKAN_DRIVERS_SWRAST:x86-x32 = ""

VULKAN_DRIVERS_VIRTIO = ",virtio"
# No atomic_uint on ARMv4 / v5
VULKAN_DRIVERS_VIRTIO:armv4 = ""
VULKAN_DRIVERS_VIRTIO:armv5 = ""

# keep sorted by the driver name (rather than PKGCONFIG)
VULKAN_DRIVERS = ""
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'amd gallium-llvm', ',amd', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'asahi libclc gallium-llvm', ',asahi', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'broadcom', ',broadcom', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'virtio', ',gfxstream', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'imagination', ',imagination', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc gallium-llvm', ',intel', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc', ',intel_hasvk', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'panfrost libclc', ',panfrost', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', '${VULKAN_DRIVERS_SWRAST}', '', d)}"
VULKAN_DRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'virtio', '${VULKAN_DRIVERS_VIRTIO}', '', d)}"

PACKAGECONFIG[vulkan] = "-Dvulkan-drivers=${@strip_comma('${VULKAN_DRIVERS}')}, -Dvulkan-drivers='',glslang-native vulkan-loader vulkan-headers"
PACKAGECONFIG[vulkan-beta] = "-Dvulkan-beta=true,-Dvulkan-beta=false"

# mesa development and testing tools support, per driver

# keep sorted by the driver name (rather than PKGCONFIG)
TOOLS = ""
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'asahi', ',asahi', '', d)}"
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'imagination', ',imagination', '', d)}"
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'lima', ',lima', '', d)}"
TOOLS .= "${@bb.utils.contains('PACKAGECONFIG', 'panfrost', ',panfrost', '', d)}"

# dependencies for tools.
TOOLS_DEPS:append = "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ' ncurses libconfig libxml2 ', '', d)}"

PACKAGECONFIG[expat] = "-Dexpat=enabled, -Dexpat=disabled, expat"
PACKAGECONFIG[tools] = "-Dtools=${@strip_comma('${TOOLS}')}, -Dtools='', ${TOOLS_DEPS}"
# Depends on expat PACKAGECONFIG!
PACKAGECONFIG[xmlconfig] = "-Dxmlconfig=enabled, -Dxmlconfig=disabled"
PACKAGECONFIG[zlib] = "-Dzlib=enabled, -Dzlib=disabled, zlib"

GALLIUMDRIVERS_LLVMPIPE = ",llvmpipe"
GALLIUMDRIVERS_SOFTPIPE = ",softpipe"
# gallium softpipe and llvmpipe was found to crash Xorg on startup in x32 qemu
GALLIUMDRIVERS_LLVMPIPE:x86-x32 = ""
GALLIUMDRIVERS_SOFTPIPE:x86-x32 = ""

# keep sorted by the driver name (rather than PKGCONFIG)
GALLIUMDRIVERS = ""
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'asahi libclc gallium-llvm', ',asahi', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc', ',crocus', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'etnaviv', ',etnaviv', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'ethosu', ',ethosu', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'freedreno', ',freedreno', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'intel gallium-llvm', ',i915', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'intel libclc gallium-llvm', ',iris', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'lima', ',lima', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', '${GALLIUMDRIVERS_LLVMPIPE}', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'nouveau gallium-llvm', ',nouveau', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'panfrost libclc', ',panfrost', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'amd gallium-llvm', ',r300', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'amd', ',r600', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'amd gallium-llvm', ',radeonsi', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'rocket', ',rocket', '', d)}"
GALLIUMDRIVERS .= "${GALLIUMDRIVERS_SOFTPIPE}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'svga gallium-llvm', ',svga', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'tegra', ',tegra', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'v3d', ',v3d', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'vc4', ',vc4', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'virtio', ',virgl', '', d)}"
GALLIUMDRIVERS .= "${@bb.utils.contains('PACKAGECONFIG', 'zink', ',zink', '', d)}"

PACKAGECONFIG[gallium] = "-Dgallium-drivers=${@strip_comma('${GALLIUMDRIVERS}')}, -Dgallium-drivers='', libdrm"
PACKAGECONFIG[gallium-llvm] = "-Dllvm=enabled -Dshared-llvm=enabled, -Dllvm=disabled, llvm llvm-native elfutils"

PACKAGECONFIG[libclc] = "-Dmesa-clc=system -Dprecomp-compiler=system,,mesa-tools-native"
PACKAGECONFIG[va] = "-Dgallium-va=enabled,-Dgallium-va=disabled,libva-initial"

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

# Provide virtual names to allow selecting preferred rproviders
RPROVIDES:mesa-vulkan-drivers += "virtual-vulkan-icd"
RPROVIDES:libopencl-mesa += "virtual-opencl-icd"

# GLES2 and GLES3 implementations are packaged in a single library in libgles2-mesa.
# Add a dependency so the GLES3 dev package is associated with its implementation.
RPROVIDES:libgles2-mesa += "libgles3-mesa"
RPROVIDES:libgles2-mesa-dev += "libgles3-mesa-dev"

RDEPENDS:libopencl-mesa += "${@bb.utils.contains('PACKAGECONFIG', 'opencl', 'libclc', '', d)}"

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
             mesa-tools \
            "

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
FILES:libegl-mesa = "${libdir}/libEGL*.so.* ${datadir}/glvnd/egl_vendor.d"
FILES:libgbm = "${libdir}/libgbm.so.* ${libdir}/gbm/*_gbm.so"
FILES:libgallium = "${libdir}/libgallium-*.so"
FILES:libgles1-mesa = "${libdir}/libGLESv1*.so.*"
FILES:libgles2-mesa = "${libdir}/libGLESv2.so.*"
FILES:libgl-mesa = "${libdir}/libGL.so.*"
FILES:libglx-mesa = "${libdir}/libGLX*.so.*"
FILES:libopencl-mesa = "${libdir}/lib*OpenCL.so* ${sysconfdir}/OpenCL/vendors/*.icd"
FILES:libglapi = "${libdir}/libglapi.so.*"

FILES:${PN}-dev = "${libdir}/pkgconfig/dri.pc ${includedir}/GL/internal/dri_interface.h ${includedir}/vulkan"
FILES:libegl-mesa-dev = "${libdir}/libEGL*.* ${includedir}/EGL ${includedir}/KHR ${libdir}/pkgconfig/egl.pc"
FILES:libgbm-dev = "${libdir}/libgbm.* ${libdir}/pkgconfig/gbm.pc ${includedir}/gbm.h ${includedir}/gbm_backend_abi.h"
FILES:libgl-mesa-dev = "${libdir}/libGL.* ${includedir}/GL/*.h ${libdir}/pkgconfig/gl.pc ${libdir}/pkgconfig/glx.pc"
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

# As of May 2025 it is known that LTO breaks Mesa, for example:
# https://gitlab.freedesktop.org/mesa/mesa/-/merge_requests/34318
LTO = ""

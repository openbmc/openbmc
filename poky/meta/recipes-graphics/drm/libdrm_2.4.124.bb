SUMMARY = "Userspace interface to the kernel DRM services"
DESCRIPTION = "The runtime library for accessing the kernel DRM services.  DRM \
stands for \"Direct Rendering Manager\", which is the kernel portion of the \
\"Direct Rendering Infrastructure\" (DRI).  DRI is required for many hardware \
accelerated OpenGL drivers."
HOMEPAGE = "http://dri.freedesktop.org"
SECTION = "x11/base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://xf86drm.c;beginline=9;endline=32;md5=c8a3b961af7667c530816761e949dc71"
PROVIDES = "drm"
DEPENDS = "libpthread-stubs"

SRC_URI = "http://dri.freedesktop.org/libdrm/${BP}.tar.xz \
          "

SRC_URI[sha256sum] = "ac36293f61ca4aafaf4b16a2a7afff312aa4f5c37c9fbd797de9e3c0863ca379"

inherit meson pkgconfig manpages

PACKAGECONFIG ??= "intel radeon amdgpu nouveau vmwgfx omap freedreno vc4 etnaviv tests install-test-programs"
PACKAGECONFIG[intel] = "-Dintel=enabled,-Dintel=disabled,libpciaccess"
PACKAGECONFIG[radeon] = "-Dradeon=enabled,-Dradeon=disabled"
PACKAGECONFIG[amdgpu] = "-Damdgpu=enabled,-Damdgpu=disabled"
PACKAGECONFIG[nouveau] = "-Dnouveau=enabled,-Dnouveau=disabled"
PACKAGECONFIG[vmwgfx] = "-Dvmwgfx=enabled,-Dvmwgfx=disabled"
PACKAGECONFIG[omap] = "-Domap=enabled,-Domap=disabled"
PACKAGECONFIG[exynos] = "-Dexynos=enabled,-Dexynos=disabled"
PACKAGECONFIG[freedreno] = "-Dfreedreno=enabled,-Dfreedreno=disabled"
PACKAGECONFIG[tegra] = "-Dtegra=enabled,-Dtegra=disabled"
PACKAGECONFIG[vc4] = "-Dvc4=enabled,-Dvc4=disabled"
PACKAGECONFIG[etnaviv] = "-Detnaviv=enabled,-Detnaviv=disabled"
PACKAGECONFIG[freedreno-kgsl] = "-Dfreedreno-kgsl=true,-Dfreedreno-kgsl=false"
PACKAGECONFIG[valgrind] = "-Dvalgrind=enabled,-Dvalgrind=disabled,valgrind"
PACKAGECONFIG[install-test-programs] = "-Dinstall-test-programs=true,-Dinstall-test-programs=false"
PACKAGECONFIG[cairo-tests] = "-Dcairo-tests=enabled,-Dcairo-tests=disabled"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"
PACKAGECONFIG[udev] = "-Dudev=true,-Dudev=false,udev"
PACKAGECONFIG[manpages] = "-Dman-pages=enabled,-Dman-pages=disabled,libxslt-native xmlto-native python3-docutils-native"

ALLOW_EMPTY:${PN}-drivers = "1"
PACKAGES =+ "${PN}-tests ${PN}-drivers ${PN}-radeon ${PN}-nouveau ${PN}-omap \
             ${PN}-intel ${PN}-exynos ${PN}-freedreno ${PN}-amdgpu \
             ${PN}-etnaviv"

RRECOMMENDS:${PN}-drivers = "${PN}-radeon ${PN}-nouveau ${PN}-omap ${PN}-intel \
                             ${PN}-exynos ${PN}-freedreno ${PN}-amdgpu \
                             ${PN}-etnaviv"

FILES:${PN}-tests = "${bindir}/*"
FILES:${PN}-radeon = "${libdir}/libdrm_radeon.so.*"
FILES:${PN}-nouveau = "${libdir}/libdrm_nouveau.so.*"
FILES:${PN}-omap = "${libdir}/libdrm_omap.so.*"
FILES:${PN}-intel = "${libdir}/libdrm_intel.so.*"
FILES:${PN}-exynos = "${libdir}/libdrm_exynos.so.*"
FILES:${PN}-freedreno = "${libdir}/libdrm_freedreno.so.*"
FILES:${PN}-amdgpu = "${libdir}/libdrm_amdgpu.so.* ${datadir}/${PN}/amdgpu.ids"
FILES:${PN}-etnaviv = "${libdir}/libdrm_etnaviv.so.*"

BBCLASSEXTEND = "native nativesdk"

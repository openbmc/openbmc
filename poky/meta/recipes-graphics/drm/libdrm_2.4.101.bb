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

SRC_URI = "http://dri.freedesktop.org/libdrm/${BP}.tar.xz"
SRC_URI[md5sum] = "e6a6f1b88963210b3d62acd7310a1cc7"
SRC_URI[sha256sum] = "ddf31baa8e49473624860bd166ce654dc349873f7a6c7b3305964249315c78a7"

inherit meson pkgconfig manpages

PACKAGECONFIG ??= "libkms intel radeon amdgpu nouveau vmwgfx omap freedreno vc4 etnaviv install-test-programs"
PACKAGECONFIG[libkms] = "-Dlibkms=true,-Dlibkms=false"
PACKAGECONFIG[intel] = "-Dintel=true,-Dintel=false,libpciaccess"
PACKAGECONFIG[radeon] = "-Dradeon=true,-Dradeon=false"
PACKAGECONFIG[amdgpu] = "-Damdgpu=true,-Damdgpu=false"
PACKAGECONFIG[nouveau] = "-Dnouveau=true,-Dnouveau=false"
PACKAGECONFIG[vmwgfx] = "-Dvmwgfx=true,-Dvmwgfx=false"
PACKAGECONFIG[omap] = "-Domap=true,-Domap=false"
PACKAGECONFIG[exynos] = "-Dexynos=true,-Dexynos=false"
PACKAGECONFIG[freedreno] = "-Dfreedreno=true,-Dfreedreno=false"
PACKAGECONFIG[tegra] = "-Dtegra=true,-Dtegra=false"
PACKAGECONFIG[vc4] = "-Dvc4=true,-Dvc4=false"
PACKAGECONFIG[etnaviv] = "-Detnaviv=true,-Detnaviv=false"
PACKAGECONFIG[freedreno-kgsl] = "-Dfreedreno-kgsl=true,-Dfreedreno-kgsl=false"
PACKAGECONFIG[valgrind] = "-Dvalgrind=true,-Dvalgrind=false,valgrind"
PACKAGECONFIG[install-test-programs] = "-Dinstall-test-programs=true,-Dinstall-test-programs=false"
PACKAGECONFIG[cairo-tests] = "-Dcairo-tests=true,-Dcairo-tests=false"
PACKAGECONFIG[udev] = "-Dudev=true,-Dudev=false,udev"
PACKAGECONFIG[manpages] = "-Dman-pages=true,-Dman-pages=false,libxslt-native xmlto-native"

ALLOW_EMPTY_${PN}-drivers = "1"
PACKAGES =+ "${PN}-tests ${PN}-drivers ${PN}-radeon ${PN}-nouveau ${PN}-omap \
             ${PN}-intel ${PN}-exynos ${PN}-kms ${PN}-freedreno ${PN}-amdgpu \
             ${PN}-etnaviv"

RRECOMMENDS_${PN}-drivers = "${PN}-radeon ${PN}-nouveau ${PN}-omap ${PN}-intel \
                             ${PN}-exynos ${PN}-freedreno ${PN}-amdgpu \
                             ${PN}-etnaviv"

FILES_${PN}-tests = "${bindir}/*"
FILES_${PN}-radeon = "${libdir}/libdrm_radeon.so.*"
FILES_${PN}-nouveau = "${libdir}/libdrm_nouveau.so.*"
FILES_${PN}-omap = "${libdir}/libdrm_omap.so.*"
FILES_${PN}-intel = "${libdir}/libdrm_intel.so.*"
FILES_${PN}-exynos = "${libdir}/libdrm_exynos.so.*"
FILES_${PN}-kms = "${libdir}/libkms*.so.*"
FILES_${PN}-freedreno = "${libdir}/libdrm_freedreno.so.*"
FILES_${PN}-amdgpu = "${libdir}/libdrm_amdgpu.so.* ${datadir}/${PN}/amdgpu.ids"
FILES_${PN}-etnaviv = "${libdir}/libdrm_etnaviv.so.*"

BBCLASSEXTEND = "native nativesdk"

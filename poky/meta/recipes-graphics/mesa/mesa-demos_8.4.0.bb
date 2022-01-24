SUMMARY = "Mesa demo applications"
DESCRIPTION = "This package includes the demonstration application, such as glxgears. \
These applications can be used for Mesa validation and benchmarking."
HOMEPAGE = "http://mesa3d.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = "file://src/xdemos/glxgears.c;beginline=1;endline=20;md5=914225785450eff644a86c871d3ae00e \
                    file://src/xdemos/glxdemo.c;beginline=1;endline=8;md5=b01d5ab1aee94d35b7efaa2ef48e1a06"

SRC_URI = "https://mesa.freedesktop.org/archive/demos/${BPN}-${PV}.tar.bz2 \
           file://0001-mesa-demos-Add-missing-data-files.patch \
           file://0003-configure-Allow-to-disable-demos-which-require-GLEW-.patch \
           file://0004-Use-DEMOS_DATA_DIR-to-locate-data-files.patch \
           file://0007-Install-few-more-test-programs.patch \
           file://0008-glsl-perf-Add-few-missing-.glsl-.vert-.frag-files-to.patch \
           file://0009-glsl-perf-Install-.glsl-.vert-.frag-files.patch \
           file://0012-mesa-demos-OpenVG-demos-with-single-frame-need-eglSw.patch \
           "
SRC_URI[md5sum] = "6b65a02622765522176d00f553086fa3"
SRC_URI[sha256sum] = "01e99c94a0184e63e796728af89bfac559795fb2a0d6f506fa900455ca5fff7d"

inherit autotools pkgconfig features_check
# depends on virtual/egl, virtual/libgl ...
REQUIRED_DISTRO_FEATURES = "opengl x11"

PACKAGECONFIG ?= "drm osmesa freetype2 gbm egl gles1 gles2 \
                  x11 glew glu"

# The Wayland code doesn't work with Wayland 1.0, so disable it for now
#${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"

EXTRA_OECONF = "--with-system-data-files"

PACKAGECONFIG[drm] = "--enable-libdrm,--disable-libdrm,libdrm"
PACKAGECONFIG[egl] = "--enable-egl,--disable-egl,virtual/egl"
PACKAGECONFIG[freetype2] = "--enable-freetype2,--disable-freetype2,freetype"
PACKAGECONFIG[gbm] = "--enable-gbm,--disable-gbm,virtual/libgl"
PACKAGECONFIG[gles1] = "--enable-gles1,--disable-gles1,virtual/libgles1"
PACKAGECONFIG[gles2] = "--enable-gles2,--disable-gles2,virtual/libgles2"
PACKAGECONFIG[glut] = "--with-glut=${STAGING_EXECPREFIXDIR},--without-glut,freeglut"
PACKAGECONFIG[osmesa] = "--enable-osmesa,--disable-osmesa,"
PACKAGECONFIG[vg] = "--enable-vg,--disable-vg,virtual/libopenvg"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,virtual/libgl wayland"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11"
PACKAGECONFIG[glew] = "--enable-glew,--disable-glew,glew"
PACKAGECONFIG[glu] = "--enable-glu,--disable-glu,virtual/libgl"

do_install:append() {
	# it can be completely empty when all PACKAGECONFIG options are disabled
	rmdir --ignore-fail-on-non-empty ${D}${bindir}

	if [ -f ${D}${bindir}/clear ]; then
        	mv ${D}${bindir}/clear ${D}${bindir}/clear.mesa-demos
	fi
}

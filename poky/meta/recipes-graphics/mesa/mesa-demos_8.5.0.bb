SUMMARY = "Mesa demo applications"
DESCRIPTION = "This package includes the demonstration application, such as glxgears. \
These applications can be used for Mesa validation and benchmarking."
HOMEPAGE = "http://mesa3d.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = "file://src/xdemos/glxgears.c;beginline=1;endline=20;md5=914225785450eff644a86c871d3ae00e \
                    file://src/xdemos/glxdemo.c;beginline=1;endline=8;md5=b01d5ab1aee94d35b7efaa2ef48e1a06"

SRC_URI = "https://mesa.freedesktop.org/archive/demos/${PV}/${BPN}-${PV}.tar.bz2 \
           file://0001-mesa-demos-Add-missing-data-files.patch \
           file://0004-Use-DEMOS_DATA_DIR-to-locate-data-files.patch \
           "
SRC_URI[sha256sum] = "cea2df0a80f09a30f635c4eb1a672bf90c5ddee0b8e77f4d70041668ef71aac1"

inherit meson pkgconfig features_check
# depends on virtual/egl, virtual/libgl ...
REQUIRED_DISTRO_FEATURES = "opengl x11"

EXTRA_OEMESON = "-Dwith-system-data-files=true"

PACKAGECONFIG ?= "drm egl gles1 gles2 \
                  ${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"

PACKAGECONFIG[drm] = "-Dlibdrm=enabled,-Dlibdrm=disabled,libdrm"
PACKAGECONFIG[egl] = "-Degl=enabled,-Degl=disabled,virtual/egl"
PACKAGECONFIG[gles1] = "-Dgles1=enabled,-Dgles1=disabled,virtual/libgles1"
PACKAGECONFIG[gles2] = "-Dgles2=enabled,-Dgles2=disabled,virtual/libgles2"
PACKAGECONFIG[glut] = "-Dwith-glut=${STAGING_EXECPREFIXDIR},,freeglut"
PACKAGECONFIG[osmesa] = "-Dosmesa=enabled,-Dosmesa=disabled,"
PACKAGECONFIG[wayland] = "-Dwayland=enabled,-Dwayland=disabled,virtual/libgl wayland wayland-native wayland-protocols"
PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,virtual/libx11 libglu"

do_install:append() {
	# it can be completely empty when all PACKAGECONFIG options are disabled
	rmdir --ignore-fail-on-non-empty ${D}${bindir}

	if [ -f ${D}${bindir}/clear ]; then
		mv ${D}${bindir}/clear ${D}${bindir}/clear.mesa-demos
	fi
}

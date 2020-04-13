SUMMARY = "OpenGL function pointer management library"
DESCRIPTION = "It hides the complexity of dlopen(), dlsym(), \
glXGetProcAddress(), eglGetProcAddress(), etc. from the app developer, with \
very little knowledge needed on their part. They get to read GL specs and \
write code using undecorated function names like glCompileShader()."
HOMEPAGE = "https://github.com/anholt/libepoxy/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=58ef4c80d401e07bd9ee8b6b58cf464b"

SRC_URI = "https://github.com/anholt/${BPN}/releases/download/${PV}/${BP}.tar.xz \
           file://0001-dispatch_common.h-define-also-EGL_NO_X11.patch \
           "
SRC_URI[md5sum] = "00f47ad447321f9dc59f85bc1c9d0467"
SRC_URI[sha256sum] = "0bd2cc681dfeffdef739cb29913f8c3caa47a88a451fd2bc6e606c02997289d2"
UPSTREAM_CHECK_URI = "https://github.com/anholt/libepoxy/releases"

inherit meson pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"
REQUIRED_DISTRO_FEATURES_class-native = ""
REQUIRED_DISTRO_FEATURES_class-nativesdk = ""

PACKAGECONFIG[egl] = "-Degl=yes, -Degl=no, virtual/egl"
PACKAGECONFIG[x11] = "-Dglx=yes, -Dglx=no -Dx11=false, virtual/libx11 virtual/libgl"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} egl"

EXTRA_OEMESON += "-Dtests=false"

PACKAGECONFIG_class-native = "egl x11"
PACKAGECONFIG_class-nativesdk = "egl x11"

BBCLASSEXTEND = "native nativesdk"

# This will ensure that dlopen will attempt only GL libraries provided by host
do_install_append_class-native() {
	chrpath --delete ${D}${libdir}/*.so
}

do_install_append_class-nativesdk() {
	chrpath --delete ${D}${libdir}/*.so
}

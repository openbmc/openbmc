SUMMARY = "WebKit web rendering engine for the GTK+ platform"
HOMEPAGE = "http://www.webkitgtk.org/"
BUGTRACKER = "http://bugs.webkit.org/"

LICENSE = "BSD & LGPLv2+"
LIC_FILES_CHKSUM = "file://Source/JavaScriptCore/COPYING.LIB;md5=d0c6d6397a5d84286dda758da57bd691 \
                    file://Source/WebKit/LICENSE;md5=4646f90082c40bcf298c285f8bab0b12 \
                    file://Source/WebCore/LICENSE-APPLE;md5=4646f90082c40bcf298c285f8bab0b12 \
		    file://Source/WebCore/LICENSE-LGPL-2;md5=36357ffde2b64ae177b2494445b79d21 \
		    file://Source/WebCore/LICENSE-LGPL-2.1;md5=a778a33ef338abbaf8b8a7c36b6eec80 \
		   "

SRC_URI = "\
  http://www.webkitgtk.org/releases/${BPN}-${PV}.tar.xz \
  file://clang.patch \
  file://0001-Enable-backtrace-on-linux-when-using-glibc.patch \
  file://0001-Fix-build-with-non-glibc-libraries-on-linux.patch \
  file://0001-FindGObjectIntrospection.cmake-prefix-variables-obta.patch \
  file://0001-When-building-introspection-files-add-CMAKE_C_FLAGS-.patch \
  file://0001-OptionsGTK.cmake-drop-the-hardcoded-introspection-gt.patch \
  "
SRC_URI[md5sum] = "84832b9d8329413b4f1d87df5f7e8efe"
SRC_URI[sha256sum] = "990d62c82ed6dede31a6ff0a82d847f16b812842ff3e1093d17113627652864e"

inherit cmake lib_package pkgconfig perlnative pythonnative distro_features_check upstream-version-is-even gobject-introspection

# depends on libxt
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "zlib libsoup-2.4 curl libxml2 cairo libxslt libxt libidn gnutls \
           gtk+3 gstreamer1.0 gstreamer1.0-plugins-base flex-native gperf-native sqlite3 \
	   pango icu bison-native gnome-common gawk intltool-native libwebp \
	   atk udev harfbuzz jpeg libpng pulseaudio librsvg libtheora libvorbis libxcomposite libxtst \
	   ruby-native libnotify gstreamer1.0-plugins-bad \
          "

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'x11', 'x11', 'wayland' ,d)} \
                   ${@base_contains('DISTRO_FEATURES', 'opengl', 'webgl', '' ,d)} \
                   enchant \
                   gtk2 \
                   libsecret \
                  "

PACKAGECONFIG[wayland] = "-DENABLE_WAYLAND_TARGET=ON,-DENABLE_WAYLAND_TARGET=OFF,wayland"
PACKAGECONFIG[x11] = "-DENABLE_X11_TARGET=ON,-DENABLE_X11_TARGET=OFF,virtual/libx11"
PACKAGECONFIG[geoclue] = "-DENABLE_GEOLOCATION=ON,-DENABLE_GEOLOCATION=OFF,geoclue"
PACKAGECONFIG[enchant] = "-DENABLE_SPELLCHECK=ON,-DENABLE_SPELLCHECK=OFF,enchant"
PACKAGECONFIG[gtk2] = "-DENABLE_PLUGIN_PROCESS_GTK2=ON,-DENABLE_PLUGIN_PROCESS_GTK2=OFF,gtk+"
PACKAGECONFIG[gles2] = "-DENABLE_GLES2=ON,-DENABLE_GLES2=OFF,virtual/libgles2"
PACKAGECONFIG[webgl] = "-DENABLE_WEBGL=ON,-DENABLE_WEBGL=OFF,virtual/libgl"
PACKAGECONFIG[libsecret] = "-DENABLE_CREDENTIAL_STORAGE=ON,-DENABLE_CREDENTIAL_STORAGE=OFF,libsecret"
PACKAGECONFIG[libhyphen] = "-DUSE_LIBHYPHEN=ON,-DUSE_LIBHYPHEN=OFF,libhyphen"

EXTRA_OECMAKE = " \
		-DPORT=GTK \
		-DCMAKE_BUILD_TYPE=Release \
		${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
		-DENABLE_GTKDOC=OFF \
		-DENABLE_MINIBROWSER=ON \
		"

# Javascript JIT is not supported on powerpc
EXTRA_OECMAKE_append_powerpc = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_powerpc64 = " -DENABLE_JIT=OFF "

# ARM JIT code does not build on ARMv5/6 anymore, apparently they test only on v7 onwards
EXTRA_OECMAKE_append_armv5 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_armv6 = " -DENABLE_JIT=OFF "

# binutils 2.25.1 has a bug on aarch64:
# https://sourceware.org/bugzilla/show_bug.cgi?id=18430
EXTRA_OECMAKE_append_aarch64 = " -DUSE_LD_GOLD=OFF "

# JIT not supported on MIPS either
EXTRA_OECMAKE_append_mips = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_mips64 = " -DENABLE_JIT=OFF "

FILES_${PN} += "${libdir}/webkit2gtk-4.0/injected-bundle/libwebkit2gtkinjectedbundle.so"

# http://errors.yoctoproject.org/Errors/Details/20370/
ARM_INSTRUCTION_SET = "arm"

# Invalid data memory access: 0x00000000
# ...
# qemu: uncaught target signal 11 (Segmentation fault) - core dumped
# Segmentation fault
EXTRA_OECMAKE_append_powerpc = " -DENABLE_INTROSPECTION=OFF "


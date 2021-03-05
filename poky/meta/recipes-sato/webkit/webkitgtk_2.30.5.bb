SUMMARY = "WebKit web rendering engine for the GTK+ platform"
HOMEPAGE = "https://www.webkitgtk.org/"
BUGTRACKER = "https://bugs.webkit.org/"

LICENSE = "BSD & LGPLv2+"
LIC_FILES_CHKSUM = "file://Source/JavaScriptCore/COPYING.LIB;md5=d0c6d6397a5d84286dda758da57bd691 \
                    file://Source/WebCore/LICENSE-APPLE;md5=4646f90082c40bcf298c285f8bab0b12 \
		    file://Source/WebCore/LICENSE-LGPL-2;md5=36357ffde2b64ae177b2494445b79d21 \
		    file://Source/WebCore/LICENSE-LGPL-2.1;md5=a778a33ef338abbaf8b8a7c36b6eec80 \
		   "

SRC_URI = "https://www.webkitgtk.org/releases/${BPN}-${PV}.tar.xz \
           file://0001-FindGObjectIntrospection.cmake-prefix-variables-obta.patch \
           file://0001-When-building-introspection-files-add-CMAKE_C_FLAGS-.patch \
           file://0001-Fix-racy-parallel-build-of-WebKit2-4.0.gir.patch \
           file://0001-Tweak-gtkdoc-settings-so-that-gtkdoc-generation-work.patch \
           file://0001-Enable-THREADS_PREFER_PTHREAD_FLAG.patch \
           file://0001-Fix-build-with-musl.patch \
           file://include_array.patch \
           file://include_xutil.patch \
           "
SRC_URI[sha256sum] = "7d0dab08e3c5ae07bec80b2822ef42e952765d5724cac86eb23999bfed5a7f1f"

inherit cmake pkgconfig gobject-introspection perlnative features_check upstream-version-is-even gtk-doc

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'opengl', '', d)}"

CVE_PRODUCT = "webkitgtk webkitgtk\+"

DEPENDS = "zlib libsoup-2.4 curl libxml2 cairo libxslt libgcrypt \
           gtk+3 gstreamer1.0 gstreamer1.0-plugins-base flex-native gperf-native sqlite3 \
	   pango icu bison-native gawk intltool-native libwebp \
	   atk udev harfbuzz jpeg libpng librsvg libtheora libvorbis \
	   ruby-native libnotify gstreamer1.0-plugins-bad \
	   gettext-native glib-2.0 glib-2.0-native libtasn1 \
          "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'webgl opengl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', 'webgl gles2', d)} \
                   enchant \
                   libsecret \
                  "

PACKAGECONFIG[wayland] = "-DENABLE_WAYLAND_TARGET=ON,-DENABLE_WAYLAND_TARGET=OFF,wayland libwpe wpebackend-fdo wayland-native"
PACKAGECONFIG[x11] = "-DENABLE_X11_TARGET=ON,-DENABLE_X11_TARGET=OFF,virtual/libx11 libxcomposite libxdamage libxrender libxt"
PACKAGECONFIG[geoclue] = "-DENABLE_GEOLOCATION=ON,-DENABLE_GEOLOCATION=OFF,geoclue"
PACKAGECONFIG[enchant] = "-DENABLE_SPELLCHECK=ON,-DENABLE_SPELLCHECK=OFF,enchant2"
PACKAGECONFIG[gles2] = "-DENABLE_GLES2=ON,-DENABLE_GLES2=OFF,virtual/libgles2"
PACKAGECONFIG[webgl] = "-DENABLE_WEBGL=ON,-DENABLE_WEBGL=OFF,virtual/libgl"
PACKAGECONFIG[opengl] = "-DENABLE_GRAPHICS_CONTEXT_GL=ON,-DENABLE_GRAPHICS_CONTEXT_GL=OFF,virtual/libgl"
PACKAGECONFIG[libsecret] = "-DUSE_LIBSECRET=ON,-DUSE_LIBSECRET=OFF,libsecret"
PACKAGECONFIG[libhyphen] = "-DUSE_LIBHYPHEN=ON,-DUSE_LIBHYPHEN=OFF,libhyphen"
PACKAGECONFIG[woff2] = "-DUSE_WOFF2=ON,-DUSE_WOFF2=OFF,woff2"
PACKAGECONFIG[openjpeg] = "-DUSE_OPENJPEG=ON,-DUSE_OPENJPEG=OFF,openjpeg"
PACKAGECONFIG[systemd] = "-DUSE_SYSTEMD=ON,-DUSE_SYSTEMD=off,systemd"

# webkitgtk is full of /usr/bin/env python, particular for generating docs
do_configure[postfuncs] += "setup_python_link"
setup_python_link() {
	if [ ! -e ${STAGING_BINDIR_NATIVE}/python ]; then
		ln -s `which python3` ${STAGING_BINDIR_NATIVE}/python
	fi
}

EXTRA_OECMAKE = " \
		-DPORT=GTK \
		-DCMAKE_BUILD_TYPE=Release \
		${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
		${@bb.utils.contains('GTKDOC_ENABLED', 'True', '-DENABLE_GTKDOC=ON', '-DENABLE_GTKDOC=OFF', d)} \
		-DENABLE_MINIBROWSER=ON \
                -DPYTHON_EXECUTABLE=`which python3` \
                -DENABLE_BUBBLEWRAP_SANDBOX=OFF \
		"

# Javascript JIT is not supported on ARC
EXTRA_OECMAKE_append_arc = " -DENABLE_JIT=OFF "
# By default 25-bit "medium" calls are used on ARC
# which is not enough for binaries larger than 32 MiB
CFLAGS_append_arc = " -mlong-calls"
CXXFLAGS_append_arc = " -mlong-calls"

# Javascript JIT is not supported on powerpc
EXTRA_OECMAKE_append_powerpc = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_powerpc64 = " -DENABLE_JIT=OFF "

# ARM JIT code does not build on ARMv4/5/6 anymore
EXTRA_OECMAKE_append_armv5 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_armv6 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE_append_armv4 = " -DENABLE_JIT=OFF "

EXTRA_OECMAKE_append_mipsarch = " -DUSE_LD_GOLD=OFF "
EXTRA_OECMAKE_append_powerpc = " -DUSE_LD_GOLD=OFF "

# JIT and gold linker does not work on RISCV
EXTRA_OECMAKE_append_riscv32 = " -DUSE_LD_GOLD=OFF -DENABLE_JIT=OFF"
EXTRA_OECMAKE_append_riscv64 = " -DUSE_LD_GOLD=OFF -DENABLE_JIT=OFF"

# JIT not supported on MIPS either
EXTRA_OECMAKE_append_mipsarch = " -DENABLE_JIT=OFF -DENABLE_C_LOOP=ON "

# JIT not supported on X32
# An attempt was made to upstream JIT support for x32 in
# https://bugs.webkit.org/show_bug.cgi?id=100450, but this was closed as
# unresolved due to limited X32 adoption.
EXTRA_OECMAKE_append_x86-x32 = " -DENABLE_JIT=OFF "

SECURITY_CFLAGS_remove_aarch64 = "-fpie"
SECURITY_CFLAGS_append_aarch64 = " -fPIE"

FILES_${PN} += "${libdir}/webkit2gtk-4.0/injected-bundle/libwebkit2gtkinjectedbundle.so"

RRECOMMENDS_${PN} += "ca-certificates shared-mime-info"

# http://errors.yoctoproject.org/Errors/Details/20370/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv6 = "arm"

# https://bugzilla.yoctoproject.org/show_bug.cgi?id=9474
# https://bugs.webkit.org/show_bug.cgi?id=159880
# JSC JIT can build on ARMv7 with -marm, but doesn't work on runtime.
# Upstream only tests regularly the JSC JIT on ARMv7 with Thumb2 (-mthumb).
ARM_INSTRUCTION_SET_armv7a = "thumb"
ARM_INSTRUCTION_SET_armv7r = "thumb"
ARM_INSTRUCTION_SET_armv7ve = "thumb"

# introspection inside qemu-arm hangs forever on musl/arm builds
# therefore disable GI_DATA
GI_DATA_ENABLED_libc-musl_armv7a = "False"
GI_DATA_ENABLED_libc-musl_armv7ve = "False"

# Can't be built with ccache
CCACHE_DISABLE = "1"

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from comments in generated sources to ensure reproducibility
        sed -i -e "s,${WORKDIR},,g" \
            ${B}/DerivedSources/webkit2gtk/webkit2/*.cpp \
            ${B}/DerivedSources/ForwardingHeaders/JavaScriptCore/*.h \
            ${B}/DerivedSources/JavaScriptCore/*.h \
            ${B}/DerivedSources/JavaScriptCore/yarr/*.h \
            ${B}/DerivedSources/MiniBrowser/*.c
}


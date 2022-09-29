SUMMARY = "WebKit web rendering engine for the GTK+ platform"
HOMEPAGE = "https://www.webkitgtk.org/"
BUGTRACKER = "https://bugs.webkit.org/"

LICENSE = "BSD-2-Clause & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Source/JavaScriptCore/COPYING.LIB;md5=d0c6d6397a5d84286dda758da57bd691 \
                    file://Source/WebCore/LICENSE-APPLE;md5=4646f90082c40bcf298c285f8bab0b12 \
                    file://Source/WebCore/LICENSE-LGPL-2;md5=36357ffde2b64ae177b2494445b79d21 \
                    file://Source/WebCore/LICENSE-LGPL-2.1;md5=a778a33ef338abbaf8b8a7c36b6eec80 \
                    "

SRC_URI = "https://www.webkitgtk.org/releases/${BPN}-${PV}.tar.xz \
           file://0001-FindGObjectIntrospection.cmake-prefix-variables-obta.patch \
           file://0001-Tweak-gtkdoc-settings-so-that-gtkdoc-generation-work.patch \
           file://0001-Fix-build-without-opengl-or-es.patch \
           file://reproducibility.patch \
           file://0001-When-building-introspection-files-do-not-quote-CFLAG.patch \
           "

SRC_URI[sha256sum] = "1193bc821946336776f0dfa5e0dca5651f1e57157eda12da4721d2441f24a61a"

inherit cmake pkgconfig gobject-introspection perlnative features_check upstream-version-is-even gtk-doc

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'opengl', '', d)}"

CVE_PRODUCT = "webkitgtk webkitgtk\+"

DEPENDS = " \
          ruby-native \
          gperf-native \
          cairo \
          harfbuzz \
          jpeg \
          atk \
          libwebp \
          gtk+3 \
          libxslt \
          libtasn1 \
          libnotify \
          gstreamer1.0 \
          gstreamer1.0-plugins-base \
          "

PACKAGECONFIG_SOUP ?= "soup2"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'webgl opengl', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', 'webgl gles2', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl-or-es', '', d)} \
                   enchant \
                   libsecret \
                   ${PACKAGECONFIG_SOUP} \
                  "

PACKAGECONFIG[wayland] = "-DENABLE_WAYLAND_TARGET=ON,-DENABLE_WAYLAND_TARGET=OFF,wayland libwpe wpebackend-fdo wayland-native"
PACKAGECONFIG[angle] = "-DUSE_ANGLE_WEBGL=ON,-DUSE_ANGLE_WEBGL=OFF"
PACKAGECONFIG[x11] = "-DENABLE_X11_TARGET=ON,-DENABLE_X11_TARGET=OFF,virtual/libx11 libxcomposite libxdamage libxrender libxt"
PACKAGECONFIG[geoclue] = "-DENABLE_GEOLOCATION=ON,-DENABLE_GEOLOCATION=OFF,geoclue"
PACKAGECONFIG[enchant] = "-DENABLE_SPELLCHECK=ON,-DENABLE_SPELLCHECK=OFF,enchant2"
PACKAGECONFIG[gles2] = "-DENABLE_GLES2=ON,-DENABLE_GLES2=OFF,virtual/libgles2"
PACKAGECONFIG[webgl] = "-DENABLE_WEBGL=ON,-DENABLE_WEBGL=OFF,virtual/egl"
PACKAGECONFIG[opengl] = "-DENABLE_GRAPHICS_CONTEXT_GL=ON,-DENABLE_GRAPHICS_CONTEXT_GL=OFF,virtual/egl"
PACKAGECONFIG[opengl-or-es] = "-DUSE_OPENGL_OR_ES=ON,-DUSE_OPENGL_OR_ES=OFF"
PACKAGECONFIG[libsecret] = "-DUSE_LIBSECRET=ON,-DUSE_LIBSECRET=OFF,libsecret"
PACKAGECONFIG[libhyphen] = "-DUSE_LIBHYPHEN=ON,-DUSE_LIBHYPHEN=OFF,libhyphen"
PACKAGECONFIG[woff2] = "-DUSE_WOFF2=ON,-DUSE_WOFF2=OFF,woff2"
PACKAGECONFIG[openjpeg] = "-DUSE_OPENJPEG=ON,-DUSE_OPENJPEG=OFF,openjpeg"
PACKAGECONFIG[systemd] = "-DUSE_SYSTEMD=ON,-DUSE_SYSTEMD=off,systemd"
PACKAGECONFIG[reduce-size] = "-DCMAKE_BUILD_TYPE=MinSizeRel,-DCMAKE_BUILD_TYPE=Release,,"
PACKAGECONFIG[lcms] = "-DUSE_LCMS=ON,-DUSE_LCMS=OFF,lcms"
PACKAGECONFIG[soup2] = "-DUSE_SOUP2=ON,-DUSE_SOUP2=OFF,libsoup-2.4,,,soup3"
PACKAGECONFIG[soup3] = ",,libsoup,,,soup2"
PACKAGECONFIG[journald] = "-DENABLE_JOURNALD_LOG=ON,-DENABLE_JOURNALD_LOG=OFF,systemd"

# webkitgtk is full of /usr/bin/env python, particular for generating docs
do_configure[postfuncs] += "setup_python_link"
setup_python_link() {
	if [ ! -e ${STAGING_BINDIR_NATIVE}/python ]; then
		ln -s `which python3` ${STAGING_BINDIR_NATIVE}/python
	fi
}

EXTRA_OECMAKE = " \
		-DPORT=GTK \
		${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
		${@bb.utils.contains('GTKDOC_ENABLED', 'True', '-DENABLE_GTKDOC=ON', '-DENABLE_GTKDOC=OFF', d)} \
		-DENABLE_MINIBROWSER=ON \
                -DPYTHON_EXECUTABLE=`which python3` \
                -DENABLE_BUBBLEWRAP_SANDBOX=OFF \
                -DENABLE_GAMEPAD=OFF \
		"

# Javascript JIT is not supported on ARC
EXTRA_OECMAKE:append:arc = " -DENABLE_JIT=OFF "
# By default 25-bit "medium" calls are used on ARC
# which is not enough for binaries larger than 32 MiB
CFLAGS:append:arc = " -mlong-calls"
CXXFLAGS:append:arc = " -mlong-calls"

# Needed for non-mesa graphics stacks when x11 is disabled
CXXFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '-DEGL_NO_X11=1', d)}"

# Javascript JIT is not supported on powerpc
EXTRA_OECMAKE:append:powerpc = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE:append:powerpc64 = " -DENABLE_JIT=OFF "

# ARM JIT code does not build on ARMv4/5/6 anymore
EXTRA_OECMAKE:append:armv5 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE:append:armv6 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE:append:armv4 = " -DENABLE_JIT=OFF "

EXTRA_OECMAKE:append:mipsarch = " -DUSE_LD_GOLD=OFF "
EXTRA_OECMAKE:append:powerpc = " -DUSE_LD_GOLD=OFF "

# JIT and gold linker does not work on RISCV
EXTRA_OECMAKE:append:riscv32 = " -DUSE_LD_GOLD=OFF -DENABLE_JIT=OFF"
EXTRA_OECMAKE:append:riscv64 = " -DUSE_LD_GOLD=OFF -DENABLE_JIT=OFF"

# JIT not supported on MIPS either
EXTRA_OECMAKE:append:mipsarch = " -DENABLE_JIT=OFF -DENABLE_C_LOOP=ON "

# JIT not supported on X32
# An attempt was made to upstream JIT support for x32 in
# https://bugs.webkit.org/show_bug.cgi?id=100450, but this was closed as
# unresolved due to limited X32 adoption.
EXTRA_OECMAKE:append:x86-x32 = " -DENABLE_JIT=OFF "

SECURITY_CFLAGS:remove:aarch64 = "-fpie"
SECURITY_CFLAGS:append:aarch64 = " -fPIE"

FILES:${PN} += "${libdir}/webkit2gtk-4.*/injected-bundle/libwebkit2gtkinjectedbundle.so"

RRECOMMENDS:${PN} += "ca-certificates shared-mime-info"

# http://errors.yoctoproject.org/Errors/Details/20370/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

# https://bugzilla.yoctoproject.org/show_bug.cgi?id=9474
# https://bugs.webkit.org/show_bug.cgi?id=159880
# JSC JIT can build on ARMv7 with -marm, but doesn't work on runtime.
# Upstream only tests regularly the JSC JIT on ARMv7 with Thumb2 (-mthumb).
ARM_INSTRUCTION_SET:armv7a = "thumb"
ARM_INSTRUCTION_SET:armv7r = "thumb"
ARM_INSTRUCTION_SET:armv7ve = "thumb"

# introspection inside qemu-arm hangs forever on musl/arm builds
# therefore disable GI_DATA
GI_DATA_ENABLED:libc-musl:armv7a = "False"
GI_DATA_ENABLED:libc-musl:armv7ve = "False"

# Can't be built with ccache
CCACHE_DISABLE = "1"

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from comments in generated sources to ensure reproducibility
        sed -i -e "s,${WORKDIR},,g" \
            ${B}/JavaScriptCore/DerivedSources/*.h \
            ${B}/JavaScriptCore/DerivedSources/yarr/*.h \
            ${B}/JavaScriptCore/PrivateHeaders/JavaScriptCore/*.h \
            ${B}/WebKit2Gtk/DerivedSources/webkit2/*.cpp \
            ${B}/WebKit2Gtk/DerivedSources/webkit2/*.h

}


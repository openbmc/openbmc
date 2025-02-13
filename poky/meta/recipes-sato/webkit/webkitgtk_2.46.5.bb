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
           file://reproducibility.patch \
           file://0001-CMake-Add-a-variable-to-control-macro-__PAS_ALWAYS_I.patch \
           file://no-musttail-arm.patch \
           file://t6-not-declared.patch \
           file://0001-Support-ICU-76.1-build.patch \
           file://bmalloc-fix.patch \
           file://sys_futex.patch \
           "
SRC_URI[sha256sum] = "bad4020bb0cfb3e740df3082c2d9cbf67cf4095596588a56aecdde6702137805"

inherit cmake pkgconfig gobject-introspection perlnative features_check upstream-version-is-even gi-docgen

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"

CVE_PRODUCT = "webkitgtk webkitgtk\+"

DEPENDS += " \
          ruby-native \
          gperf-native \
          unifdef-native \
          cairo \
          harfbuzz \
          jpeg \
          atk \
          libwebp \
          gtk4 \
          libxslt \
          libtasn1 \
          libnotify \
          gstreamer1.0 \
          gstreamer1.0-plugins-base \
          glib-2.0-native \
          gettext-native \
          "

PACKAGECONFIG_SOUP ?= "soup3"
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
PACKAGECONFIG[avif] = "-DUSE_AVIF_LOG=ON,-DUSE_AVIF=OFF,libavif"
PACKAGECONFIG[media-recorder] = "-DENABLE_MEDIA_RECORDER=ON,-DENABLE_MEDIA_RECORDER=OFF,gstreamer1.0-plugins-bad"
PACKAGECONFIG[jpegxl] = "-DUSE_JPEGXL=ON,-DUSE_JPEGXL=OFF,libjxl"
PACKAGECONFIG[backtrace] = "-DUSE_LIBBACKTRACE=ON,-DUSE_LIBBACKTRACE=OFF,libbacktrace"
PACKAGECONFIG[gamepad] = "-DENABLE_GAMEPAD=ON,-DENABLE_GAMEPAD=OFF,libmanette"
PACKAGECONFIG[sysprof-capture] = "-DUSE_SYSTEM_SYSPROF_CAPTURE=YES,-DUSE_SYSTEM_SYSPROF_CAPTURE=NO,sysprof"

EXTRA_OECMAKE = " \
                 -DPORT=GTK \
                 ${@oe.utils.vartrue('GI_DATA_ENABLED', '-DENABLE_INTROSPECTION=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
                 ${@oe.utils.vartrue('GIDOCGEN_ENABLED', '-DENABLE_DOCUMENTATION=ON', '-DENABLE_DOCUMENTATION=OFF', d)} \
                 ${@oe.utils.vartrue('DEBUG_BUILD', '-DWEBKIT_NO_INLINE_HINTS=ON', '-DWEBKIT_NO_INLINE_HINTS=OFF', d)} \
                 -DENABLE_MINIBROWSER=ON \
                 -DENABLE_BUBBLEWRAP_SANDBOX=OFF \
                 -DUSE_GTK4=ON \
                 -DCMAKE_EXPORT_COMPILE_COMMANDS=OFF \
                 -DENABLE_RELEASE_LOG=ON \
                 "

# Unless DEBUG_BUILD is enabled, pass -g1 to massively reduce the size of the
# debug symbols (4.3GB to 700M at time of writing)
DEBUG_LEVELFLAG = "-g1"

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
EXTRA_OECMAKE:append:armv4 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE:append:armv5 = " -DENABLE_JIT=OFF "
EXTRA_OECMAKE:append:armv6 = " -DENABLE_JIT=OFF "

# JIT does not work on RISCV
EXTRA_OECMAKE:append:riscv32 = " -DENABLE_JIT=OFF"
EXTRA_OECMAKE:append:riscv64 = " -DENABLE_JIT=OFF"

# JIT not supported on MIPS either
EXTRA_OECMAKE:append:mipsarch = " -DENABLE_JIT=OFF -DENABLE_C_LOOP=ON "

# JIT not supported on X32
# An attempt was made to upstream JIT support for x32 in
# https://bugs.webkit.org/show_bug.cgi?id=100450, but this was closed as
# unresolved due to limited X32 adoption.
EXTRA_OECMAKE:append:x86-x32 = " -DENABLE_JIT=OFF "

SECURITY_CFLAGS:remove:aarch64 = "-fpie"
SECURITY_CFLAGS:append:aarch64 = " -fPIE"

FILES:${PN} += "${libdir}/webkitgtk-*/injected-bundle/*.so"

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

# ANGLE requires SSE support as of webkit 2.40.x on 32 bit x86
COMPATIBLE_HOST:x86 = "${@bb.utils.contains_any('TUNE_FEATURES', 'core2 corei7', '.*', 'null', d)}"

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
            ${B}/WebCore/DerivedSources/*.cpp \
            ${B}/WebKitGTK/DerivedSources/webkit/*.cpp
}


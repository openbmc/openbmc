SUMMARY = "WebKit web rendering engine for the GTK+ platform"
DESCRIPTION = "\
    This recipe builds with Gtk3 (instead of Gtk4) which results in API version 4.1 \
    (having javascriptcoregtk-4.1.pc instead of 6.0). \
"
HOMEPAGE = "https://www.webkitgtk.org/"
BUGTRACKER = "https://bugs.webkit.org/"

LICENSE = "BSD-2-Clause & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Source/JavaScriptCore/COPYING.LIB;md5=d0c6d6397a5d84286dda758da57bd691 \
                    file://Source/WebCore/LICENSE-APPLE;md5=4646f90082c40bcf298c285f8bab0b12 \
                    file://Source/WebCore/LICENSE-LGPL-2;md5=36357ffde2b64ae177b2494445b79d21 \
                    file://Source/WebCore/LICENSE-LGPL-2.1;md5=a778a33ef338abbaf8b8a7c36b6eec80 \
                    "

SRC_URI = "https://www.webkitgtk.org/releases/webkitgtk-${PV}.tar.xz \
           file://0001-FindGObjectIntrospection.cmake-prefix-variables-obta.patch \
           file://reproducibility.patch \
           file://no-musttail-arm.patch \
           file://sys_futex.patch \
           file://0001-Fix-build-errors-on-RISCV-https-bugs.webkit.org-show.patch \
           file://fix-ftbfs-riscv64.patch \
           file://0001-CMake-Add-a-variable-to-control-macro-__PAS_ALWAYS_I.patch \
           file://0001-Fix-32bit-arm.patch \
           "
SRC_URI[sha256sum] = "8737631bac3e9c7ad3e5208f9370e076c09d9c45b39980021ce54edadcc6f94f"

inherit cmake pkgconfig gobject-introspection perlnative features_check upstream-version-is-even gi-docgen

S = "${UNPACKDIR}/webkitgtk-${PV}"

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
          gtk+3 \
          libxslt \
          libtasn1 \
          libnotify \
          gstreamer1.0 \
          gstreamer1.0-plugins-base \
          glib-2.0-native \
          gettext-native \
          libsoup \
          libepoxy \
          "

PACKAGECONFIG ??= " \
	   ${@bb.utils.filter('DISTRO_FEATURES', 'systemd wayland x11', d)} \
	   ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'webgl opengl', '', d)} \
	   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', 'webgl gles2', d)} \
	   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'opengl-or-es', '', d)} \
	   enchant \
	   libsecret \
	   "

PACKAGECONFIG[wayland] = "-DENABLE_WAYLAND_TARGET=ON,-DENABLE_WAYLAND_TARGET=OFF,wayland libwpe wpebackend-fdo wayland-native"
PACKAGECONFIG[angle] = "-DUSE_ANGLE_WEBGL=ON,-DUSE_ANGLE_WEBGL=OFF"
PACKAGECONFIG[x11] = "-DENABLE_X11_TARGET=ON,-DENABLE_X11_TARGET=OFF,virtual/libx11 libxcomposite libxdamage libxrender libxt"
PACKAGECONFIG[geoclue] = "-DENABLE_GEOLOCATION=ON,-DENABLE_GEOLOCATION=OFF,geoclue"
PACKAGECONFIG[enchant] = "-DENABLE_SPELLCHECK=ON,-DENABLE_SPELLCHECK=OFF,enchant2"
PACKAGECONFIG[gles2] = "-DENABLE_GLES2=ON,-DENABLE_GLES2=OFF,virtual/libgles2"
PACKAGECONFIG[jpegxl] = " -DUSE_JPEGXL=ON,-DUSE_JPEGXL=OFF,libjxl"
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
PACKAGECONFIG[journald] = "-DENABLE_JOURNALD_LOG=ON,-DENABLE_JOURNALD_LOG=OFF,systemd"
PACKAGECONFIG[avif] = "-DUSE_AVIF_LOG=ON,-DUSE_AVIF=OFF,libavif"
PACKAGECONFIG[media-recorder] = "-DENABLE_MEDIA_RECORDER=ON,-DENABLE_MEDIA_RECORDER=OFF,gstreamer1.0-plugins-bad"
PACKAGECONFIG[gamepad] = "-DENABLE_GAMEPAD=ON,-DENABLE_GAMEPAD=OFF,libmanette"
PACKAGECONFIG[webrtc] = "-DENABLE_WEB_RTC=ON,-DENABLE_WEB_RTC=OFF"
PACKAGECONFIG[bubblewrap] = "-DENABLE_BUBBLEWRAP_SANDBOX=ON -DBWRAP_EXECUTABLE=${bindir}/bwrap -DDBUS_PROXY_EXECUTABLE=${bindir}/xdg-dbus-proxy,-DENABLE_BUBBLEWRAP_SANDBOX=OFF,,bubblewrap xdg-dbus-proxy"
PACKAGECONFIG[backtrace] = "-DUSE_LIBBACKTRACE=ON,-DUSE_LIBBACKTRACE=OFF,libbacktrace"
PACKAGECONFIG[sysprof-capture] = "-DUSE_SYSTEM_SYSPROF_CAPTURE=YES,-DUSE_SYSTEM_SYSPROF_CAPTURE=NO,sysprof"
PACKAGECONFIG[speech] = "-DENABLE_SPEECH_SYNTHESIS=ON,-DENABLE_SPEECH_SYNTHESIS=OFF,flite"

EXTRA_OECMAKE = " \
	-DPORT=GTK \
	-DUSE_GTK4=OFF \
	${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
	${@bb.utils.contains('GIDOCGEN_ENABLED', 'True', '-DENABLE_DOCUMENTATION=ON', '-DENABLE_DOCUMENTATION=OFF', d)} \
        ${@oe.utils.vartrue('DEBUG_BUILD', '-DWEBKIT_NO_INLINE_HINTS=ON', '-DWEBKIT_NO_INLINE_HINTS=OFF', d)} \
	-DENABLE_MINIBROWSER=ON \
	-DCMAKE_EXPORT_COMPILE_COMMANDS=OFF \
        -DUSE_SOUP2=OFF \
		"
# pass -g1 to massively reduce the size of the debug symbols (4.3GB to 700M at time of writing)
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

# ANGLE requires SSE support as of webkit 2.40.x on 32 bit x86
COMPATIBLE_HOST:x86 = "${@bb.utils.contains_any('TUNE_FEATURES', 'core2 corei7', '.*', 'null', d)}"

# introspection inside qemu-arm hangs forever on musl/arm builds
# therefore disable GI_DATA
GI_DATA_ENABLED:libc-musl:armv7a = "False"
GI_DATA_ENABLED:libc-musl:armv7ve = "False"

do_install:append() {
	mv ${D}${bindir}/WebKitWebDriver ${D}${bindir}/WebKitWebDriver3
}

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


# complation fails with Clang-21 issue - https://github.com/llvm/llvm-project/issues/132322
# Can be removed when clang is updated to v22
TOOLCHAIN:arm = "gcc"

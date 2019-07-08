require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://run-ptest \
           file://uclibc_musl_translation.patch \
           file://Enable-more-tests-while-cross-compiling.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0001-Do-not-ignore-return-value-of-write.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           file://0001-Revert-Use-absolute-paths-in-pkg-config-files.patch \
           file://0001-Set-host_machine-correctly-when-building-with-mingw3.patch \
           file://0001-Do-not-write-bindir-into-pkg-config-files.patch \
           file://0001-meson.build-do-not-hardcode-linux-as-the-host-system.patch \
           file://0001-meson-do-a-build-time-check-for-strlcpy-before-attem.patch \
           file://glib-meson.cross \
           "

SRC_URI_append_class-native = " file://relocate-modules.patch"

SRC_URI[md5sum] = "8058c7bde846dcffe5fa453eca366d73"
SRC_URI[sha256sum] = "8f43c31767e88a25da72b52a40f3301fefc49a665b56dc10ee7cc9565cbe7481"

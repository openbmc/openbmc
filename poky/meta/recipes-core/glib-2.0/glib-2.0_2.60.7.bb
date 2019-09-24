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
           file://0001-Set-host_machine-correctly-when-building-with-mingw3.patch \
           file://0001-Do-not-write-bindir-into-pkg-config-files.patch \
           file://0001-meson.build-do-not-hardcode-linux-as-the-host-system.patch \
           file://0001-meson-do-a-build-time-check-for-strlcpy-before-attem.patch \
           "

SRC_URI_append_class-native = " file://relocate-modules.patch"
SRC_URI_append_class-target = " file://glib-meson.cross"

SRC_URI[md5sum] = "f036f78a7fca330d9f7d939fcf794bde"
SRC_URI[sha256sum] = "8b12c0af569afd3b71200556ad751bad4cf4bf7bc4b5f880638459a42ca86310"

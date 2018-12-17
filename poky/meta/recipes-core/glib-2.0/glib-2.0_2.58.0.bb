require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://configure-libtool.patch \
           file://run-ptest \
           file://uclibc_musl_translation.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://Enable-more-tests-while-cross-compiling.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0001-Do-not-ignore-return-value-of-write.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           file://date-lt.patch \
           "

SRC_URI_append_class-native = " file://relocate-modules.patch"

SRC_URI[md5sum] = "9b5d21c802f55bf37171c4fbfe7d32d1"
SRC_URI[sha256sum] = "c0f4ce0730b4f95c47b711613b5406a887c2ee13ea6d25930d72a4fa7fdb77f6"

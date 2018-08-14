require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://configure-libtool.patch \
           file://run-ptest \
           file://ptest-paths.patch \
           file://uclibc_musl_translation.patch \
           file://allow-run-media-sdX-drive-mount-if-username-root.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://Enable-more-tests-while-cross-compiling.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0001-Do-not-ignore-return-value-of-write.patch \
           file://0001-Test-for-pthread_getname_np-before-using-it.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           "

SRC_URI_append_class-native = " file://relocate-modules.patch"

SRC_URI[md5sum] = "16e886ad677bf07b7d48eb8188bcf759"
SRC_URI[sha256sum] = "963fdc6685dc3da8e5381dfb9f15ca4b5709b28be84d9d05a9bb8e446abac0a8"

require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://configure-libtool.patch \
           file://fix-conflicting-rand.patch \
           file://glib-2.0-configure-readlink.patch \
           file://run-ptest \
           file://ptest-paths.patch \
           file://uclibc_musl_translation.patch \
           file://0001-configure.ac-Do-not-use-readlink-when-cross-compilin.patch \
           file://allow-run-media-sdX-drive-mount-if-username-root.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://Enable-more-tests-while-cross-compiling.patch \
           file://gi-exclude.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://ignore-format-nonliteral-warning.patch \
           file://0001-Do-not-ignore-return-value-of-write.patch \
           file://0002-tests-Ignore-y2k-warnings.patch \
           "

SRC_URI_append_class-native = " file://glib-gettextize-dir.patch \
                                file://relocate-modules.patch"

SRC_URI[md5sum] = "7f815d6e46df68e070cb421ed7f1139e"
SRC_URI[sha256sum] = "5031722e37036719c1a09163cc6cf7c326e4c4f1f1e074b433c156862bd733db"

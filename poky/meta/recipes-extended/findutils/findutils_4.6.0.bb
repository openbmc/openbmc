require findutils.inc

# GPLv2+ (<< 4.2.32), GPLv3+ (>= 4.2.32)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

DEPENDS = "bison-native"

SRC_URI += "file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
            file://0001-find-make-delete-honour-the-ignore_readdir_race-opti.patch \
            file://findutils-4.6.0-gnulib-fflush.patch \
            file://findutils-4.6.0-gnulib-makedev.patch \
"

SRC_URI[md5sum] = "9936aa8009438ce185bea2694a997fc1"
SRC_URI[sha256sum] = "ded4c9f73731cd48fec3b6bdaccce896473b6d8e337e9612e16cf1431bb1169d"

# http://savannah.gnu.org/bugs/?27299
CACHED_CONFIGUREVARS += "${@bb.utils.contains('DISTRO_FEATURES', 'libc-posix-clang-wchar', 'gl_cv_func_wcwidth_works=yes', '', d)}"

EXTRA_OECONF += "ac_cv_path_SORT=${bindir}/sort"

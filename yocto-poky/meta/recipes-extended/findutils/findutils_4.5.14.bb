require findutils.inc

# GPLv2+ (<< 4.2.32), GPLv3+ (>= 4.2.32)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"
 
DEPENDS = "bison-native"

SRC_URI += "file://0001-Unset-need_charset_alias-when-building-for-musl.patch"

SRC_URI[md5sum] = "a8a8176282fd28e8d1234c84d847fa66"
SRC_URI[sha256sum] = "0de3cf625a5c9f154eee3171e072515ffdde405244dd00502af617ac57b73ae2"

# http://savannah.gnu.org/bugs/?27299
CACHED_CONFIGUREVARS += "${@bb.utils.contains('DISTRO_FEATURES', 'libc-posix-clang-wchar', 'gl_cv_func_wcwidth_works=yes', '', d)}"

EXTRA_OECONF += "ac_cv_path_SORT=${bindir}/sort"

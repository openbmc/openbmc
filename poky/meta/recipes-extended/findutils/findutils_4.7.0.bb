require findutils.inc

# GPLv2+ (<< 4.2.32), GPLv3+ (>= 4.2.32)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = "bison-native"

SRC_URI[md5sum] = "731356dec4b1109b812fecfddfead6b2"
SRC_URI[sha256sum] = "c5fefbdf9858f7e4feb86f036e1247a54c79fc2d8e4b7064d5aaa1f47dfa789a"

# http://savannah.gnu.org/bugs/?27299
CACHED_CONFIGUREVARS += "gl_cv_func_wcwidth_works=yes"

EXTRA_OECONF += "ac_cv_path_SORT=${bindir}/sort"

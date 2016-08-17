DESCRIPTION = "Regular expressions library. The characteristics of this \
library is that different character encoding for every regular expression \
object can be specified."
HOMEPAGE = "http://www.geocities.jp/kosako3/oniguruma/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=0d4861b5bc0c392a5aa90d9d76ebd86f"

SRC_URI = "http://www.geocities.jp/kosako3/oniguruma/archive/onig-${PV}.tar.gz \
           file://do-not-use-system-headers.patch \
           file://configure.patch"

SRC_URI[md5sum] = "0d4eda2066d3c92970842a6790ce897a"
SRC_URI[sha256sum] = "c3bba66b2a84760e6582c40881db97c839d94f327870009724bb8b4d0c051f2a"

DEPENDS = "libevent"

inherit autotools binconfig

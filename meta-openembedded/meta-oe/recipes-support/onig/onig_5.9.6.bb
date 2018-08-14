DESCRIPTION = "Regular expressions library. The characteristics of this \
library is that different character encoding for every regular expression \
object can be specified."
HOMEPAGE = "https://web.archive.org/web/20150807014439/http://www.geocities.jp/kosako3/oniguruma/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=0d4861b5bc0c392a5aa90d9d76ebd86f"

SRC_URI = "https://web.archive.org/web/20150807014439/http://www.geocities.jp/kosako3/oniguruma/archive/${BP}.tar.gz \
           file://do-not-use-system-headers.patch \
           file://configure.patch"

SRC_URI[md5sum] = "d08f10ea5c94919780e6b7bed1ef9830"
SRC_URI[sha256sum] = "d5642010336a6f68b7f2e34b1f1cb14be333e4d95c2ac02b38c162caf44e47a7"

BINCONFIG = "${bindir}/onig-config"

inherit autotools binconfig-disabled

BBCLASSEXTEND = "native"

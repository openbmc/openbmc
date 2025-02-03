SUMMARY = "x86 (SSE) assembler supporting NASM and GAS-syntaxes"
LICENSE = "MIT"
HOMEPAGE = "http://www.tortall.net/projects/yasm/"

LIC_FILES_CHKSUM = "file://COPYING;md5=a12d8903508fb6bfd49d8d82c6170dd9"

DEPENDS += "flex-native bison-native"
PACKAGECONFIG[docs] = ",,xmlto-native,"

PV = "1.3.0+git"
# v1.3.0-87
SRCREV = "121ab150b3577b666c79a79f4a511798d7ad2432"
SRC_URI = "git://github.com/yasm/yasm.git;branch=master;protocol=https \
           file://0001-yasm-Set-build-date-to-SOURCE_DATE_EPOCH.patch \
           file://0002-yasm-Use-BUILD_DATE-for-reproducibility.patch \
"

S = "${WORKDIR}/git"

inherit autotools gettext python3native

CACHED_CONFIGUREVARS = "CCLD_FOR_BUILD='${CC_FOR_BUILD}'"

BBCLASSEXTEND = "native"

PARALLEL_MAKE = ""

do_configure:prepend() {
     # Don't include $CC (which includes path to sysroot) in generated header.
     sed -i -e "s/^echo \"\/\* generated \$ac_cv_stdint_message \*\/\" >>\$ac_stdint$"// ${S}/m4/ax_create_stdint_h.m4
}

CVE_STATUS_GROUPS += "CVE_STATUS_HASH_UPDATE"
CVE_STATUS_HASH_UPDATE = "CVE-2021-33454 CVE-2023-31975 CVE-2023-37732"
CVE_STATUS_HASH_UPDATE[status] = "fixed-version: patched in current git hash"

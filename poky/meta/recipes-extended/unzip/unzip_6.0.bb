SUMMARY = "Utilities for extracting and viewing files in .zip archives"
HOMEPAGE = "http://www.info-zip.org"
DESCRIPTION = "Info-ZIP's purpose is to provide free, portable, high-quality versions of the Zip and UnZip compressor-archiver utilities that are compatible with the DOS-based PKZIP by PKWARE, Inc."
SECTION = "console/utils"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94caec5a51ef55ef711ee4e8b1c69e29"
PE = "1"

SRC_URI = "${SOURCEFORGE_MIRROR}/infozip/UnZip%206.x%20%28latest%29/UnZip%206.0/unzip60.tar.gz \
	file://avoid-strip.patch \
	file://define-ldflags.patch \
	file://06-unzip60-alt-iconv-utf8_CVE-2015-1315.patch \
	file://cve-2014-9636.patch \
	file://09-cve-2014-8139-crc-overflow.patch \
	file://10-cve-2014-8140-test-compr-eb.patch \
	file://11-cve-2014-8141-getzip64data.patch \
	file://CVE-2015-7696.patch \
	file://CVE-2015-7697.patch \
	file://fix-security-format.patch \
	file://18-cve-2014-9913-unzip-buffer-overflow.patch \
	file://19-cve-2016-9844-zipinfo-buffer-overflow.patch \
	file://symlink.patch \
	file://0001-unzip-fix-CVE-2018-1000035.patch \
	file://CVE-2018-18384.patch \
	file://CVE-2019-13232_p1.patch \
	file://CVE-2019-13232_p2.patch \
	file://CVE-2019-13232_p3.patch \
	file://unzip_optimization.patch \
        file://0001-configure-Pass-LDFLAGS-to-tests-doing-link-step.patch \
        file://CVE-2021-4217.patch \
        file://CVE-2022-0529.patch \
        file://CVE-2022-0530.patch \
        file://0001-configure-Add-correct-system-headers-and-prototypes-.patch \
        file://0001-unix-configure-fix-detection-for-cross-compilation.patch \
"
UPSTREAM_VERSION_UNKNOWN = "1"

SRC_URI[md5sum] = "62b490407489521db863b523a7f86375"
SRC_URI[sha256sum] = "036d96991646d0449ed0aa952e4fbe21b476ce994abc276e49d30e686708bd37"

CVE_STATUS[CVE-2008-0888] = "fixed-version: Patch from https://bugzilla.redhat.com/attachment.cgi?id=293893&action=diff applied to 6.0 source"

# exclude version 5.5.2 which triggers a false positive
UPSTREAM_CHECK_REGEX = "unzip(?P<pver>(?!552).+)\.tgz"

S = "${WORKDIR}/unzip60"

# Makefile uses CF_NOOPT instead of CFLAGS.  We lifted the values from
# Makefile and add CFLAGS.  Optimization will be overriden by unzip
# configure to be -O3.
#
EXTRA_OEMAKE = "-e MAKEFLAGS= STRIP=true LF2='' \
                'CF_NOOPT=-I. -Ibzip2 -DUNIX ${CFLAGS}'"

export LD = "${CC}"
LD:class-native = "${CC}"

do_compile() {
        oe_runmake -f unix/Makefile generic
}

do_install() {
        oe_runmake -f unix/Makefile install prefix=${D}${prefix}
	install -d ${D}${mandir}
	mv ${D}${prefix}/man/* ${D}${mandir}
	rmdir ${D}${prefix}/man/
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "unzip"
ALTERNATIVE_LINK_NAME[unzip] = "${bindir}/unzip"

BBCLASSEXTEND = "native nativesdk"

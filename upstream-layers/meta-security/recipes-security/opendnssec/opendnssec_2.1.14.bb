SUMMARY = "OpenDNSSEC is a policy-based zone signer that automates the process of keeping track of DNSSEC keys and the signing of zones"
HOMEPAGE = "https://www.opendnssec.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b041dbe2da80d4efd951393fbba90937"

DEPENDS = "libxml2 openssl ldns libmicrohttpd jansson libyaml "

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/opendnssec-${PV}.tar.gz \
           file://libxml2_conf.patch \
           file://libdns_conf_fix.patch \
           file://0001-include-utilities.h.patch \
           file://0002-reorder-header-files-and-include-time.h.patch \
           file://0001-Fix-implicit-function-declarations.patch \
"

SRC_URI[sha256sum] = "5a68d62ea0ea3a6c61e9f4946f462c7b907fbe6bccc9e8a721b7fe0f906f95d0"

inherit autotools pkgconfig perlnative github-releases

EXTRA_OECONF = " --with-libxml2=${STAGING_DIR_HOST}/usr --with-ldns=${STAGING_DIR_HOST}/usr \
                 --with-ssl=${STAGING_DIR_HOST}/usr  "

CFLAGS += "-fcommon"

PACKAGECONFIG ?= "sqlite3"

PACKAGECONFIG[cunit] = "--with-cunit=${STAGING_DIR_HOST}/usr, --without-cunit,"
PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_DIR_HOST}/usr, ,sqlite3, sqlite3"
PACKAGECONFIG[mysql] = "--with-mysql=yes, , mariadb, mariadb"
PACKAGECONFIG[readline]  = "--with-readline, --without-readline, readline"
PACKAGECONFIG[unwind] = "--with-libunwind, --without-libunwind"

do_install:append () {
    rm -rf ${D}${localstatedir}/run
}

RDEPENDS:${PN} = "softhsm"

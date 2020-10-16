SUMMARY = "OpenDNSSEC is a policy-based zone signer that automates the process of keeping track of DNSSEC keys and the signing of zones"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b041dbe2da80d4efd951393fbba90937"

DEPENDS = "libxml2 openssl ldns libmicrohttpd jansson libyaml "

SRC_URI = "git://github.com/opendnssec/opendnssec;branch=develop \
           file://libxml2_conf.patch \
           file://libdns_conf_fix.patch \
           file://fix_fprint.patch \
           "

SRCREV = "5876bccb38428790e2e9afc806ca68b029879874"

inherit autotools pkgconfig perlnative

S = "${WORKDIR}/git"

EXTRA_OECONF = " --with-libxml2=${STAGING_DIR_HOST}/usr --with-ldns=${STAGING_DIR_HOST}/usr \
                 --with-ssl=${STAGING_DIR_HOST}/usr  "

CFLAGS += "-fcommon"

PACKAGECONFIG ?= "sqlite3"

PACKAGECONFIG[cunit] = "--with-cunit=${STAGING_DIR_HOST}/usr, --without-cunit,"
PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_DIR_HOST}/usr, ,sqlite3, sqlite3"
PACKAGECONFIG[mysql] = "--with-mysql=yes, , mariadb, mariadb"
PACKAGECONFIG[readline]  = "--with-readline, --without-readline, readline"
PACKAGECONFIG[unwind] = "--with-libunwind, --without-libunwind"

do_install_append () {
    rm -rf ${D}${localstatedir}/run
}

RDEPENDS_${PN} = "softhsm"

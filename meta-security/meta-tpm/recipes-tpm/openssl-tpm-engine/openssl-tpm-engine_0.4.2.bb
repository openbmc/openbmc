DESCRIPTION = "OpenSSL secure engine based on TPM hardware"
HOMEPAGE = "https://sourceforge.net/projects/trousers/"
SECTION = "security/tpm"

LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11f0ee3af475c85b907426e285c9bb52"

DEPENDS += "openssl trousers"

SRC_URI = "\
    git://git.code.sf.net/p/trousers/openssl_tpm_engine \
    file://0001-create-tpm-key-support-well-known-key-option.patch \
    file://0002-libtpm-support-env-TPM_SRK_PW.patch \
    file://0003-Fix-not-building-libtpm.la.patch \
    file://0003-tpm-openssl-tpm-engine-parse-an-encrypted-tpm-SRK-pa.patch \
    file://0004-tpm-openssl-tpm-engine-change-variable-c-type-from-c.patch \
"
SRCREV = "bbc2b1af809f20686e0d3553a62f0175742c0d60"

S = "${WORKDIR}/git"

inherit autotools-brokensep

# The definitions below are used to decrypt the srk password.
# It is allowed to define the values in 3 forms: string, hex number and
# the hybrid, e.g,
# srk_dec_pw = "incendia"
# srk_dec_pw = "\x69\x6e\x63\x65\x6e\x64\x69\x61"
# srk_dec_pw = "\x1""nc""\x3""nd""\x1""a"
#
# Due to the limit of escape character, the hybrid must be written in
# above style. The actual values defined below in C code style are:
# srk_dec_pw[] = { 0x01, 'n', 'c', 0x03, 'n', 'd', 0x01, 'a' };
# srk_dec_salt[] = { 'r', 0x00, 0x00, 't' };
srk_dec_pw ?= "\\"\\\x1\\"\\"nc\\"\\"\\\x3\\"\\"nd\\"\\"\\\x1\\"\\"a\\""
srk_dec_salt ?= "\\"r\\"\\"\\\x00\\\x00\\"\\"t\\""

CFLAGS_append += "-DSRK_DEC_PW=${srk_dec_pw} -DSRK_DEC_SALT=${srk_dec_salt}"

# Uncomment below line if using the plain srk password for development
#CFLAGS_append += "-DTPM_SRK_PLAIN_PW"

do_configure_prepend() {
    cd "${S}"
    cp LICENSE COPYING
    touch NEWS AUTHORS ChangeLog
}

do_install_append() {
    install -m 0755 -d "${D}${libdir}/engines"
    install -m 0755 -d "${D}${prefix}/local/ssl/lib/engines"
    install -m 0755 -d "${D}${libdir}/ssl/engines"

    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/libtpm.so.0"
    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/engines/libtpm.so"
    cp -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${prefix}/local/ssl/lib/engines/libtpm.so"
    mv -f "${D}${libdir}/openssl/engines/libtpm.so.0.0.0" "${D}${libdir}/ssl/engines/libtpm.so"
    mv -f "${D}${libdir}/openssl/engines/libtpm.la" "${D}${libdir}/ssl/engines/libtpm.la"
    rm -rf "${D}${libdir}/openssl"
}

FILES_${PN}-staticdev += "${libdir}/ssl/engines/libtpm.la"
FILES_${PN}-dbg += "\
    ${libdir}/ssl/engines/.debug \
    ${libdir}/engines/.debug \
    ${prefix}/local/ssl/lib/engines/.debug \
"
FILES_${PN} += "\
    ${libdir}/ssl/engines/libtpm.so* \
    ${libdir}/engines/libtpm.so* \
    ${libdir}/libtpm.so* \
    ${prefix}/local/ssl/lib/engines/libtpm.so* \
"

RDEPENDS_${PN} += "libcrypto libtspi"

INSANE_SKIP_${PN} = "libdir"
INSANE_SKIP_${PN}-dbg = "libdir"

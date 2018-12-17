DESCRIPTION = "OpenSSL secure engine based on TPM hardware"
HOMEPAGE = "https://github.com/mgerstner/openssl_tpm_engine"
SECTION = "security/tpm"

LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11f0ee3af475c85b907426e285c9bb52"

DEPENDS += "openssl trousers"

SRC_URI = "\
    git://github.com/mgerstner/openssl_tpm_engine.git \
    file://0001-create-tpm-key-support-well-known-key-option.patch \
    file://0002-libtpm-support-env-TPM_SRK_PW.patch \
    file://0003-tpm-openssl-tpm-engine-parse-an-encrypted-tpm-SRK-pa.patch \
    file://0004-tpm-openssl-tpm-engine-change-variable-c-type-from-c.patch \
    file://openssl11_build_fix.patch \
"
SRCREV = "b28de5065e6eb9aa5d5afe2276904f7624c2cbaf"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

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
    cd ${B}
    cp LICENSE COPYING
    touch NEWS AUTHORS ChangeLog README
}

FILES_${PN}-staticdev += "${libdir}/ssl/engines-1.1/tpm.la"
FILES_${PN}-dbg += "\
    ${libdir}/ssl/engines-1.1/.debug \
    ${libdir}/engines-1.1/.debug \
    ${prefix}/local/ssl/lib/engines-1.1/.debug \
"
FILES_${PN} += "\
    ${libdir}/ssl/engines-1.1/tpm.so* \
    ${libdir}/engines-1.1/tpm.so* \
    ${libdir}/libtpm.so* \
    ${prefix}/local/ssl/lib/engines-1.1/tpm.so* \
"

RDEPENDS_${PN} += "libcrypto libtspi"

INSANE_SKIP_${PN} = "libdir"
INSANE_SKIP_${PN}-dbg = "libdir"

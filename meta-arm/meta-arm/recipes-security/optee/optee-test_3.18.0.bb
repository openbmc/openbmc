require optee-test.inc

SRC_URI:append = " \
    file://musl-workaround.patch \
   "
SRCREV = "da5282a011b40621a2cf7a296c11a35c833ed91b"

EXTRA_OEMAKE:append:libc-musl = " OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR}"
DEPENDS:append:libc-musl = " openssl"
CFLAGS:append:libc-musl = " -Wno-error=deprecated-declarations"

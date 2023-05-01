require optee-test.inc

SRC_URI:append = " \
    file://musl-workaround.patch \
   "
SRCREV = "5db8ab4c733d5b2f4afac3e9aef0a26634c4b444"

EXTRA_OEMAKE:append:libc-musl = " OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR}"
DEPENDS:append:libc-musl = " openssl"
CFLAGS:append:libc-musl = " -Wno-error=deprecated-declarations"

# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

DEPENDS += "\
    cmake-native \
    newlib \
    stubdom-gmp \
"

# nothing to configure
do_configure[noexec] = "1"

export CMAKE_C_FLAGS = "\
    -std=c99 \
    -DTPM_NO_EXTERN \
    ${STUBDOM_CPPFLAGS} \
    ${STUBDOM_CFLAGS} \
    -Wno-declaration-after-statement \
    -Wno-implicit-fallthrough \
"

do_compile() {
    CC="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT}" cmake .. -DCMAKE_C_FLAGS:STRING="${CMAKE_C_FLAGS}"
    ${MAKE} VERBOSE=1 tpm_crypto tpm
}

do_install() {
    install -d ${D}/${libdir}
    install -m 644 -t ${D}/${libdir} ${B}/crypto/libtpm_crypto.a
    install -m 644 -t ${D}/${libdir} ${B}/tpm/libtpm.a

    install -D -m 644 -t ${D}/${includedir}/tpm-emulator/build ${S}/build/config.h
    install -D -m 644 -t ${D}/${includedir}/tpm-emulator/crypto ${S}/crypto/*.h
    install -D -m 644 -t ${D}/${includedir}/tpm-emulator/tpm ${S}/tpm/*.h
}

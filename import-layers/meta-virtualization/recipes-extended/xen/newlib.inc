# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

CPPFLAGS_INCLUDE_DIR = "-isystem `${HOST_PREFIX}gcc -print-file-name=include`"
STUBDOM_CFLAGS += "-D_I386MACH_ALLOW_HW_INTERRUPTS"

export ac_cv_path_CC_FOR_TARGET="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT} ${STUBDOM_CPPFLAGS} ${STUBDOM_CFLAGS}"
export CC_FOR_TARGET="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT} ${STUBDOM_CPPFLAGS} ${STUBDOM_CFLAGS}"

export ac_cv_path_CXX_FOR_TARGET="${HOST_PREFIX}g++ --sysroot=${RECIPE_SYSROOT}"
export CXX_FOR_TARGET="${HOST_PREFIX}g++ --sysroot=${RECIPE_SYSROOT}"

export ac_cv_path_CPP_FOR_TARGET="${HOST_PREFIX}gcc -E --sysroot=${RECIPE_SYSROOT}"
export CPP_FOR_TARGET="${HOST_PREFIX}gcc -E --sysroot=${RECIPE_SYSROOT}"

export ac_cv_path_LD_FOR_TARGET="${HOST_PREFIX}ld --sysroot=${RECIPE_SYSROOT}"
export LD_FOR_TARGET="${HOST_PREFIX}ld --sysroot=${RECIPE_SYSROOT}"

export ac_cv_path_AS_FOR_TARGET="${HOST_PREFIX}as"
export AS_FOR_TARGET="${HOST_PREFIX}as"

export ac_cv_path_AR_FOR_TARGET="${HOST_PREFIX}ar"
export AR_FOR_TARGET="${HOST_PREFIX}ar"

export ac_cv_path_NM_FOR_TARGET="${HOST_PREFIX}nm"
export NM_FOR_TARGET="${HOST_PREFIX}nm"

export ac_cv_path_RANLIB_FOR_TARGET="${HOST_PREFIX}ranlib"
export RANLIB_FOR_TARGET="${HOST_PREFIX}ranlib"

export ac_cv_path_OBJDUMP_FOR_TARGET="${HOST_PREFIX}objdump"
export OBJDUMP_FOR_TARGET="${HOST_PREFIX}objdump"

export ac_cv_path_OBJCOPY_FOR_TARGET="${HOST_PREFIX}objcopy"
export OBJCOPY_FOR_TARGET="${HOST_PREFIX}objcopy"

export ac_cv_path_STRIP_FOR_TARGET="${HOST_PREFIX}strip"
export STRIP_FOR_TARGET="${HOST_PREFIX}strip"

export ac_cv_path_STRINGS_FOR_TARGET="${HOST_PREFIX}strings"
export STRINGS_FOR_TARGET="${HOST_PREFIX}strings"

export ac_cv_path_READELF_FOR_TARGET="${HOST_PREFIX}readelf"
export READELF_FOR_TARGET="${HOST_PREFIX}readelf"

do_configure() {
    ${S}/configure --verbose --prefix=${prefix} --target=${GNU_TARGET_ARCH}-xen-elf --enable-newlib-io-long-long --disable-multilib
    find ${S} -type f | xargs perl -i.bak -pe 's/\b_(tzname|daylight|timezone)\b/$1/g'
}

do_compile() {
    ${MAKE}
}

do_install() {
    ${MAKE} DESTDIR=${D} install
    rm -rf ${D}/cross-root-${GNU_TARGET_ARCH}/share
    rm -rf ${D}/cross-root-${GNU_TARGET_ARCH}/info
    install -m 644 `${HOST_PREFIX}gcc -print-file-name=include`/stdarg.h -t ${D}/${includedir}
    install -m 644 `${HOST_PREFIX}gcc -print-file-name=include`/stddef.h -t ${D}/${includedir}
    install -m 644 `${HOST_PREFIX}gcc -print-file-name=include`/stdbool.h -t ${D}/${includedir}
}

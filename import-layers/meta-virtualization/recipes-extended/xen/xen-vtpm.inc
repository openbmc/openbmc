# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

DEPENDS = "\
    newlib \
    lwip \
    mini-os \
    polarssl \
    stubdom-gmp \
    tpm-emulator \
"
# These were unset by stubdom.inc to allow us to scope them per recipe
export CC="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT}"
export CCLD="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT}"
export CXX="${HOST_PREFIX}g++ --sysroot=${RECIPE_SYSROOT}"
export CPP="${HOST_PREFIX}gcc -E --sysroot=${RECIPE_SYSROOT}"
export LD="${HOST_PREFIX}ld --sysroot=${RECIPE_SYSROOT}"
export LD_LTO="${HOST_PREFIX}ld --sysroot=${RECIPE_SYSROOT}"
export AS="${HOST_PREFIX}as"
export AR="${HOST_PREFIX}ar"
export NM="${HOST_PREFIX}nm"
export RANLIB="${HOST_PREFIX}ranlib"
export OBJDUMP="${HOST_PREFIX}objdump"
export OBJCOPY="${HOST_PREFIX}objcopy"
export STRIP="${HOST_PREFIX}strip"
export STRINGS="${HOST_PREFIX}strings"
export READELF="${HOST_PREFIX}readelf"


# Required for some of the config stuff
export STUBDOM_TARGETS="vtpm vtpmmgr"

VTPM_CPPFLAGS = "\
    -I${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include/tpm-emulator/build \
    -I${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include/tpm-emulator/crypto \
    -I${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include/tpm-emulator/tpm \
    -I${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include/tpm-emulator \
"

# The includes from this Xen directory are not in the MiniOS repo, although they probably should be.
STUBDOM_CPPFLAGS += "-isystem ${B}/include/"

do_configure() {

    # GCC 7 fails linking header defined inlines if not declared 'static' or 'extern'
    # This appears to be fixed in Xen 4.10.0+, so let's look at version of Xen source
    # to determine if we need to modify inline declarations.
    #
    # 'echo -e' to enable interpretation of backslashes
    # 'sort -V' to natural sort version numbers
    # 'head -n1' to capture the first line of output from sort command

    if [ "${PV}" = "$(echo "${PV};4.9.999" | sed 's/;/\n/' | sort -V | head -n1)" ]; then
        sed -i "s/^inline/static inline/g" ${B}/vtpmmgr/*.h
    fi

    for i in AR AS NM RANLIB OBJDUMP OBJCOPY STRIP STRINGS READELF CXX LD LD_LTO CC CPP; do
        sed -i "s/^\($i\s\s*\).*=/\1?=/" ${MINIOS_SRCDIR}/Config.mk
    done

    # replicate the TARGETS_MINIOS target in xen/stubdom/Makefile
    for i in ${STUBDOM_TARGETS}; do
        [ -d ${B}/mini-os-${XEN_TARGET_ARCH}-$i ] ||
        for j in $(cd ${MINIOS_SRCDIR} ; find . -type d) ; do \
                mkdir -p ${B}/mini-os-${XEN_TARGET_ARCH}-$i/$j; \
        done
    done
}

do_compile() {
    ${MAKE} MINIOS_CONFIG="${B}/vtpm/minios.cfg" CONFIG_FILE="${B}/vtpm-minios-config.mk" DESTDIR= -C ${MINIOS_SRCDIR} config
    CPPFLAGS="`cat ${B}/vtpm-minios-config.mk` ${STUBDOM_CPPFLAGS} ${VTPM_CPPFLAGS}" CFLAGS="${STUBDOM_CFLAGS}" ${MAKE} -C ${B}/vtpm
    DEF_CPPFLAGS="${STUBDOM_CPPFLAGS}" \
        DEF_CFLAGS="${STUBDOM_CFLAGS}" \
        DEF_LDFLAGS="${STUBDOM_LDFLAGS}" \
        MINIOS_CONFIG="${B}/vtpm/minios.cfg" \
        ${MAKE} -C ${MINIOS_SRCDIR} \
        OBJ_DIR=${B}/mini-os-${XEN_TARGET_ARCH}-vtpm \
        APP_OBJS="${B}/vtpm/vtpm.a" \
        APP_LDLIBS="-ltpm -ltpm_crypto -lgmp -lpolarssl"

    ${MAKE} MINIOS_CONFIG="${B}/vtpmmgr/minios.cfg" CONFIG_FILE="${B}/vtpmmgr-minios-config.mk" DESTDIR= -C ${MINIOS_SRCDIR} config
    CPPFLAGS="`cat ${B}/vtpmmgr-minios-config.mk` ${STUBDOM_CPPFLAGS}" CFLAGS="${STUBDOM_CFLAGS}" ${MAKE} -C ${B}/vtpmmgr
    DEF_CPPFLAGS="${STUBDOM_CPPFLAGS}" \
        DEF_CFLAGS="${STUBDOM_CFLAGS}" \
        DEF_LDFLAGS="${STUBDOM_LDFLAGS}" \
        MINIOS_CONFIG="${B}/vtpmmgr/minios.cfg" \
        ${MAKE} -C ${MINIOS_SRCDIR} \
        OBJ_DIR=${B}/mini-os-${XEN_TARGET_ARCH}-vtpmmgr \
        APP_OBJS="${B}/vtpmmgr/vtpmmgr.a" \
        APP_LDLIBS="-lm -lpolarssl"
}

PACKAGES = "\
    ${PN}-vtpm-stubdom \
    ${PN}-vtpmmgr-stubdom \
"
FILES_${PN}-vtpm-stubdom="\
    ${libdir}/xen/boot/vtpm-stubdom.gz \
"

FILES_${PN}-vtpmmgr-stubdom="\
    ${libdir}/xen/boot/vtpmmgr-stubdom.gz \
"

do_install() {
    install -m 644 -D ${B}/mini-os-${XEN_TARGET_ARCH}-vtpm/mini-os.gz ${D}${libdir}/xen/boot/vtpm-stubdom.gz
    install -m 644 -D ${B}/mini-os-${XEN_TARGET_ARCH}-vtpmmgr/mini-os.gz ${D}${libdir}/xen/boot/vtpmmgr-stubdom.gz
}

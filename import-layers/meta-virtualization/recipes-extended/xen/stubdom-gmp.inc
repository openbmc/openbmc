# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

DEPENDS += "\
    newlib \
"

do_configure() {
    # need to modify prefix here during configure otherwise OE-level variables
    # (prefix, libdir, includedir, etc...) defined in stubdom.inc get messed up
    CPPFLAGS="-isystem ${RECIPE_SYSROOT}/cross-root-${GNU_TARGET_ARCH}/${GNU_TARGET_ARCH}-xen-elf/include ${STUBDOM_CPPFLAGS}" \
    CFLAGS="${STUBDOM_CFLAGS}" \
    CC="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT}" \
    ${S}/configure \
        --disable-shared \
        --enable-static \
        --disable-fft \
        --without-readline \
        --with-gnu-ld \
        --prefix=${prefix}/${GNU_TARGET_ARCH}-xen-elf \
        --libdir=${libdir} \
        --build=`${HOST_PREFIX}gcc -dumpmachine` \
        --host=${GNU_TARGET_ARCH}-xen-elf

    sed -i 's/#define HAVE_OBSTACK_VPRINTF 1/\/\/#define HAVE_OBSTACK_VPRINTF 1/' ${S}/config.h
}

do_compile() {
    ${MAKE}
}

do_install() {
    ${MAKE} DESTDIR=${D} install
    rm -rf ${D}${prefix}/${GNU_TARGET_ARCH}-xen-elf/share
}

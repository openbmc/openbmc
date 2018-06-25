# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require stubdom.inc

DEPENDS += "\
    newlib \
"
STUBDOM_CFLAGS += "\
    -Wno-memset-elt-size \
    -Wno-implicit-fallthrough \
"

# nothing to configure
do_configure[noexec] = "1"

do_compile() {
    ${MAKE} CC="${HOST_PREFIX}gcc --sysroot=${RECIPE_SYSROOT} ${STUBDOM_CPPFLAGS} ${STUBDOM_CFLAGS}"
}

do_install() {
    install -d ${D}${includedir}
    cp -r -t ${D}${includedir} ${S}/include/polarssl

    install -d ${D}/${libdir}
    install -m 644 -t ${D}/${libdir} ${S}/library/libpolarssl.a
}

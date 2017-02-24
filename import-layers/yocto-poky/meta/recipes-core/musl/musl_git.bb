# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require musl.inc

SRCREV = "39494a273eaa6b714e0fa0c59ce7a1f5fbc80a1e"

PV = "1.1.15+git${SRCPV}"

# mirror is at git://github.com/kraj/musl.git

SRC_URI = "git://git.musl-libc.org/musl \
           file://0001-Make-dynamic-linker-a-relative-symlink-to-libc.patch \
          "

S = "${WORKDIR}/git"

PROVIDES += "virtual/libc virtual/${TARGET_PREFIX}libc-for-gcc virtual/libiconv virtual/libintl"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc-initial \
           libgcc-initial \
           linux-libc-headers \
           bsd-headers \
          "

export CROSS_COMPILE="${TARGET_PREFIX}"

LDFLAGS += "-Wl,-soname,libc.so"

CONFIGUREOPTS = " \
    --prefix=${prefix} \
    --exec-prefix=${exec_prefix} \
    --bindir=${bindir} \
    --libdir=${libdir} \
    --includedir=${includedir} \
    --syslibdir=${base_libdir} \
"

do_configure() {
	${S}/configure ${CONFIGUREOPTS}
}

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR='${D}'

	install -d ${D}${bindir}
	ln -s ../../${libdir}/libc.so ${D}${bindir}/ldd
	for l in crypt dl m pthread resolv rt util xnet
	do
		ln -s libc.so ${D}${libdir}/lib$l.so
	done
}

RDEPENDS_${PN}-dev += "linux-libc-headers-dev bsd-headers-dev"
RPROVIDES_${PN}-dev += "libc-dev virtual-libc-dev"
RPROVIDES_${PN} += "ldd libsegfault rtld(GNU_HASH)"

LEAD_SONAME = "libc.so"

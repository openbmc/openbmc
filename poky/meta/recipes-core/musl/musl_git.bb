# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require musl.inc
inherit linuxloader

SRCREV = "c50985d5c8e316c5c464f352e79eeebfed1121a9"

PV = "1.1.20+git${SRCPV}"

# mirror is at git://github.com/kraj/musl.git

SRC_URI = "git://git.musl-libc.org/musl \
           file://0001-Make-dynamic-linker-a-relative-symlink-to-libc.patch \
          "

S = "${WORKDIR}/git"

PROVIDES += "virtual/libc virtual/${TARGET_PREFIX}libc-for-gcc virtual/libiconv virtual/libintl virtual/crypt"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc-initial \
           libgcc-initial \
           linux-libc-headers \
           bsd-headers \
           libssp-nonshared \
          "
GLIBC_LDSO = "${@get_glibc_loader(d)}"

export CROSS_COMPILE="${TARGET_PREFIX}"

LDFLAGS += "-Wl,-soname,libc.so"

# When compiling for Thumb or Thumb2, frame pointers _must_ be disabled since the
# Thumb frame pointer in r7 clashes with musl's use of inline asm to make syscalls
# (where r7 is used for the syscall NR). In most cases, frame pointers will be
# disabled automatically due to the optimisation level, but append an explicit
# -fomit-frame-pointer to handle cases where optimisation is set to -O0 or frame
# pointers have been enabled by -fno-omit-frame-pointer earlier in CFLAGS, etc.
CFLAGS_append_arm = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

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
	rm -f ${D}${bindir}/ldd
	lnr ${D}${libdir}/libc.so ${D}${bindir}/ldd
	lnr ${D}${libdir}/libc.so ${D}${GLIBC_LDSO}
	for l in crypt dl m pthread resolv rt util xnet
	do
		ln -sf libc.so ${D}${libdir}/lib$l.so
	done
	for i in libc.so.6 libcrypt.so.1 libdl.so.2 libm.so.6 libpthread.so.0 libresolv.so.2 librt.so.1 libutil.so.1; do
		ln -sf libc.so ${D}${libdir}/$i
	done
}

PACKAGES =+ "${PN}-glibc-compat"

FILES_${PN}-glibc-compat += "\
                ${libdir}/libc.so.6 ${libdir}/libcrypt.so.1 \
                ${libdir}/libdl.so.2 ${libdir}/libm.so.6 \
                ${libdir}/libpthread.so.0 ${libdir}/libresolv.so.2 \
                ${libdir}/librt.so.1 ${libdir}/libutil.so.1 \
                ${GLIBC_LDSO} \
                "

RDEPENDS_${PN}-dev += "linux-libc-headers-dev bsd-headers-dev libssp-nonshared-staticdev"
RPROVIDES_${PN}-dev += "libc-dev virtual-libc-dev"
RPROVIDES_${PN} += "ldd libsegfault rtld(GNU_HASH)"

LEAD_SONAME = "libc.so"

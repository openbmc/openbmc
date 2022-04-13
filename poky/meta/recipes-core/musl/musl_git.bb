# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require musl.inc
inherit linuxloader

SRCREV = "7a43f6fea9081bdd53d8a11cef9e9fab0348c53d"

BASEVER = "1.2.3"

PV = "${BASEVER}+git${SRCPV}"

# mirror is at git://github.com/kraj/musl.git

SRC_URI = "git://git.musl-libc.org/musl;branch=master \
           file://0001-Make-dynamic-linker-a-relative-symlink-to-libc.patch \
           file://0002-ldso-Use-syslibdir-and-libdir-as-default-pathes-to-l.patch \
          "

S = "${WORKDIR}/git"

PROVIDES += "virtual/libc virtual/libiconv virtual/libintl virtual/crypt"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc \
           libgcc-initial \
           linux-libc-headers \
           bsd-headers \
           libssp-nonshared \
          "
GLIBC_LDSO = "${@get_glibc_loader(d)}"
MUSL_LDSO_ARCH = "${@get_musl_loader_arch(d)}"

export CROSS_COMPILE="${TARGET_PREFIX}"

LDFLAGS += "-Wl,-soname,libc.so"

# When compiling for Thumb or Thumb2, frame pointers _must_ be disabled since the
# Thumb frame pointer in r7 clashes with musl's use of inline asm to make syscalls
# (where r7 is used for the syscall NR). In most cases, frame pointers will be
# disabled automatically due to the optimisation level, but append an explicit
# -fomit-frame-pointer to handle cases where optimisation is set to -O0 or frame
# pointers have been enabled by -fno-omit-frame-pointer earlier in CFLAGS, etc.
CFLAGS:append:arm = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

CONFIGUREOPTS = " \
    --prefix=${prefix} \
    --exec-prefix=${exec_prefix} \
    --bindir=${bindir} \
    --libdir=${libdir} \
    --includedir=${includedir} \
    --syslibdir=/lib \
"

do_configure() {
	${S}/configure ${CONFIGUREOPTS}
}

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR='${D}'
	install -d ${D}${bindir} ${D}/lib ${D}${sysconfdir}
        echo "${base_libdir}" > ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
        echo "${libdir}" >> ${D}${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path
	rm -f ${D}${bindir}/ldd ${D}${GLIBC_LDSO}
	ln -rs ${D}${libdir}/libc.so ${D}${bindir}/ldd
}

FILES:${PN} += "/lib/ld-musl-${MUSL_LDSO_ARCH}.so.1 ${sysconfdir}/ld-musl-${MUSL_LDSO_ARCH}.path"
FILES:${PN}-staticdev = "${libdir}/libc.a"
FILES:${PN}-dev =+ "${libdir}/libcrypt.a ${libdir}/libdl.a ${libdir}/libm.a \
                    ${libdir}/libpthread.a ${libdir}/libresolv.a \
                    ${libdir}/librt.a ${libdir}/libutil.a ${libdir}/libxnet.a \
                   "

RDEPENDS:${PN}-dev += "linux-libc-headers-dev bsd-headers-dev libssp-nonshared-staticdev"
RPROVIDES:${PN}-dev += "libc-dev virtual-libc-dev"
RPROVIDES:${PN} += "ldd rtld(GNU_HASH)"

LEAD_SONAME = "libc.so"
INSANE_SKIP:${PN}-dev = "staticdev"
INSANE_SKIP:${PN} = "libdir"

UPSTREAM_CHECK_COMMITS = "1"

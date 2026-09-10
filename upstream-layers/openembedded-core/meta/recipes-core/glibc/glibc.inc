require glibc-common.inc
require glibc-ld.inc

DEPENDS = "virtual/cross-cc virtual/cross-binutils libgcc-initial linux-libc-headers gawk-replacement-native"

PROVIDES = "virtual/libc"
PROVIDES += "virtual/libintl virtual/libiconv"

inherit autotools texinfo

inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'nscd', 'systemd update-rc.d', '', d)}

LEAD_SONAME = "libc.so"

# msgfmt could come from gettext-native but we don't depend on that and
# disable for reproducibility
CACHED_CONFIGUREVARS += " \
  ac_cv_path_BASH_SHELL=${base_bindir}/bash \
  ac_cv_prog_MSGFMT= \
  libc_cv_slibdir=${base_libdir} \
  libc_cv_rootsbindir=${base_sbindir} \
  libc_cv_localedir=${localedir} \
  libc_cv_include_x86_isa_level=no \
"

# ifunc doesn't appear to work on mips, casuses libbfd assertion failures
CACHED_CONFIGUREVARS:append:mipsarch = " libc_cv_ld_gnu_indirect_function=no"

GLIBC_EXTRA_OECONF ?= ""
GLIBC_EXTRA_OECONF:class-nativesdk = ""

TARGET_CC_ARCH:append:toolchain-gcc = " -fno-link-libatomic"
SDK_CC_ARCH:append:toolchain-gcc = " -fno-link-libatomic"

do_configure:prepend() {
	sed -e "s#/bin/bash#/bin/sh#" -i ${S}/elf/ldd.bash.in
}

# Enable backtrace from abort()
do_configure:append:arm () {
	echo "CFLAGS-abort.c = -fasynchronous-unwind-tables" >> ${B}/configparms
	echo "CFLAGS-raise.c = -fasynchronous-unwind-tables" >> ${B}/configparms
}

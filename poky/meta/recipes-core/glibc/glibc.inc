require glibc-common.inc
require glibc-ld.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc libgcc-initial linux-libc-headers"

PROVIDES = "virtual/libc"
PROVIDES += "virtual/libintl virtual/libiconv"
inherit autotools texinfo systemd

LEAD_SONAME = "libc.so"

# msgfmt could come from gettext-native but we don't depend on that and
# disable for reproducibility
CACHED_CONFIGUREVARS += " \
  ac_cv_path_BASH_SHELL=${base_bindir}/bash \
  ac_cv_prog_MSGFMT= \
  libc_cv_slibdir=${base_libdir} \
  libc_cv_rootsbindir=${base_sbindir} \
  libc_cv_localedir=${localedir} \
  libc_cv_ssp_strong=no \
  libc_cv_ssp_all=no \
  libc_cv_ssp=no \
"

# ifunc doesn't appear to work on mips, casuses libbfd assertion failures
CACHED_CONFIGUREVARS_append_mipsarch = " libc_cv_ld_gnu_indirect_function=no"

GLIBC_EXTRA_OECONF ?= ""
GLIBC_EXTRA_OECONF_class-nativesdk = ""

# glibc uses PARALLELMFLAGS variable to pass parallel build info so transfer
# PARALLEL_MAKE into PARALLELMFLAGS and empty out PARALLEL_MAKE
EGLIBCPARALLELISM := "PARALLELMFLAGS="${PARALLEL_MAKE}""
EXTRA_OEMAKE[vardepsexclude] += "EGLIBCPARALLELISM"
EXTRA_OEMAKE += "${EGLIBCPARALLELISM}"
PARALLEL_MAKE = ""

# glibc make-syscalls.sh has a number of issues with /bin/dash and
# it's output which make calls via the SHELL also has issues, so
# ensure make uses /bin/bash
EXTRA_OEMAKE += "SHELL=/bin/bash"

do_configure_prepend() {
	sed -e "s#@BASH@#/bin/sh#" -i ${S}/elf/ldd.bash.in
}

# Enable backtrace from abort()
do_configure_append_arm () {
	echo "CFLAGS-abort.c = -fasynchronous-unwind-tables" >> ${B}/configparms
	echo "CFLAGS-raise.c = -fasynchronous-unwind-tables" >> ${B}/configparms
}

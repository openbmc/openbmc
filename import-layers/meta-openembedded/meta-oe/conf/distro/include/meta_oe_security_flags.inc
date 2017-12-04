# Build errors with the pie options enabled
SECURITY_CFLAGS_pn-libdbus-c++ = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-lvm2 = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-rrdtool = "${SECURITY_NO_PIE_CFLAGS}"

# This has text reloc errors with the pie options enabled
SECURITY_CFLAGS_pn-llvm3.3 = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-mozjs = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-openldap = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-s3c64xx-gpio = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-s3c24xx-gpio = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-cpufrequtils = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libcec = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libmodplug = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libcdio = "${SECURITY_NO_PIE_CFLAGS}"

#| /mnt/b/build/tmp-glibc/sysroots/intel-corei7-64/usr/lib/libc_nonshared.a(elf-init.oS): In function `__libc_csu_init':
#| /usr/src/debug/glibc/2.24-r0/git/csu/elf-init.c:86: undefined reference to `__init_array_start'

SECURITY_CFLAGS_pn-libvdpau = "${SECURITY_NO_PIE_CFLAGS}"

#| /mnt/a/build/tmp-glibc/sysroots/x86_64-linux/usr/libexec/x86_64-oe-linux/gcc/x86_64-oe-linux/6.2.0/ld: lj_err_dyn.o: relocation R_X86_64_TPOFF32 against `static_uex' can not be used when making a shared object; recompile with -fPIC
#| /mnt/a/build/tmp-glibc/sysroots/x86_64-linux/usr/libexec/x86_64-oe-linux/gcc/x86_64-oe-linux/6.2.0/ld: final link failed: Nonrepresentable section on output
#| collect2: error: ld returned 1 exit status
#| make[1]: *** [Makefile:675: libluajit.so] Error 1
SECURITY_CFLAGS_pn-luajit = "${SECURITY_NO_PIE_CFLAGS}"


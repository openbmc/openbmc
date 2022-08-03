SUMMARY = "OP-TEE Secure Partion Development Kit"
DESCRIPTION = "Open Portable Trusted Execution Environment - Development Kit to run secure partitions"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173"

inherit deploy python3native
require optee.inc
FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"

CVE_PRODUCT = "linaro:op-tee op-tee:op-tee_os"

DEPENDS = "python3-pyelftools-native"

DEPENDS:append:toolchain-clang = " compiler-rt"

# spdevkit isn't yet merged to master
SRC_URI = "git://git.trustedfirmware.org/OP-TEE/optee_os.git;protocol=https;branch=psa-development \
    file://0006-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0007-allow-setting-sysroot-for-clang.patch \
"
SRCREV = "f9de2c9520ed97b89760cc4c99424aae440b63f4"
PV = "3.10+git${SRCPV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    PLATFORM=${OPTEEMACHINE} \
    CFG_${OPTEE_CORE}_core=y \
    CROSS_COMPILE_core=${HOST_PREFIX} \
    CROSS_COMPILE_sp_${OPTEE_ARCH}=${HOST_PREFIX} \
    CFG_CORE_FFA=y \
    CFG_WITH_SP=y \
    O=${B} \
"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
CPPFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_configure[noexec] = "1"

do_compile() {
    oe_runmake -C ${S} sp_dev_kit
}
do_compile[cleandirs] = "${B}"

do_install() {
    #install SP devkit
    install -d ${D}${includedir}/optee/export-user_sp/
    for f in ${B}/export-sp_${OPTEE_ARCH}/* ; do
        cp -aR $f ${D}${includedir}/optee/export-user_sp/
    done
    cat > ${D}${includedir}/optee/export-user_sp/include/stddef.h <<'EOF'
#ifndef STDDEF_H
#define STDDEF_H

#include <stddef_.h>

#ifndef _PTRDIFF_T
typedef long ptrdiff_t;
#define _PTRDIFF_T
#endif

#ifndef NULL
#define NULL ((void *) 0)
#endif

#define offsetof(st, m) __builtin_offsetof(st, m)

#endif /* STDDEF_H */
EOF
    cat > ${D}${includedir}/optee/export-user_sp/include/stddef_.h <<'EOF'
#ifndef STDDEF__H
#define STDDEF__H

#ifndef SIZET_
typedef unsigned long size_t;
#define SIZET_
#endif

#endif /* STDDEF__H */
EOF
    cat > ${D}${includedir}/optee/export-user_sp/include/stdarg.h <<'EOF'
#ifndef STDARG_H
#define STDARG_H

#define va_list __builtin_va_list
#define va_start(ap, last) __builtin_va_start(ap, last)
#define va_end(ap) __builtin_va_end(ap)
#define va_copy(to, from) __builtin_va_copy(to, from)
#define va_arg(to, type) __builtin_va_arg(to, type)

#endif /* STDARG_H */
EOF
    cat > ${D}${includedir}/optee/export-user_sp/include/stdbool.h <<'EOF'
#ifndef STDBOOL_H
#define STDBOOL_H

#define bool	_Bool

#define true	1
#define false	0

#define __bool_true_false_are_defined	1

#endif /* STDBOOL_H */
EOF

cat > ${D}${includedir}/optee/export-user_sp/include/features.h <<'EOF'
    #ifndef _FEATURES_H
    #define _FEATURES_H
    #if defined(_ALL_SOURCE) && !defined(_GNU_SOURCE)
    #define _GNU_SOURCE 1
#endif
    #if defined(_DEFAULT_SOURCE) && !defined(_BSD_SOURCE)
    #define _BSD_SOURCE 1
#endif
    #if !defined(_POSIX_SOURCE) && !defined(_POSIX_C_SOURCE) \
     && !defined(_XOPEN_SOURCE) && !defined(_GNU_SOURCE) \
     && !defined(_BSD_SOURCE) && !defined(__STRICT_ANSI__)
    #define _BSD_SOURCE 1
    #define _XOPEN_SOURCE 700
#endif
    #if __STDC_VERSION__ >= 199901L
    #define __restrict restrict
    #elif !defined(__GNUC__)
    #define __restrict
#endif
    #if __STDC_VERSION__ >= 199901L || defined(__cplusplus)
    #define __inline inline
    #elif !defined(__GNUC__)
    #define __inline
#endif
    #if __STDC_VERSION__ >= 201112L
    #elif defined(__GNUC__)
    #define _Noreturn __attribute__((__noreturn__))
#else
    #define _Noreturn
#endif
    #define __REDIR(x,y) __typeof__(x) x __asm__(#y)
#endif
EOF
cat > ${D}${includedir}/optee/export-user_sp/include/errno.h <<'EOF'
    #ifndef _ERRNO_H
    #define _ERRNO_H
    #include <features.h>
    #define EPERM            1
    #define ENOENT           2
    #define ESRCH            3
    #define EINTR            4
    #define EIO              5
    #define ENXIO            6
    #define E2BIG            7
    #define ENOEXEC          8
    #define EBADF            9
    #define ECHILD          10
    #define EAGAIN          11
    #define ENOMEM          12
    #define EACCES          13
    #define EFAULT          14
    #define ENOTBLK         15
    #define EBUSY           16
    #define EEXIST          17
    #define EXDEV           18
    #define ENODEV          19
    #define ENOTDIR         20
    #define EISDIR          21
    #define EINVAL          22
    #define ENFILE          23
    #define EMFILE          24
    #define ENOTTY          25
    #define ETXTBSY         26
    #define EFBIG           27
    #define ENOSPC          28
    #define ESPIPE          29
    #define EROFS           30
    #define EMLINK          31
    #define EPIPE           32
    #define EDOM            33
    #define ERANGE          34
    #define EDEADLK         35
    #define ENAMETOOLONG    36
    #define ENOLCK          37
    #define ENOSYS          38
    #define ENOTEMPTY       39
    #define ELOOP           40
    #define EWOULDBLOCK     EAGAIN
    #define ENOMSG          42
    #define EIDRM           43
    #define ECHRNG          44
    #define EL2NSYNC        45
    #define EL3HLT          46
    #define EL3RST          47
    #define ELNRNG          48
    #define EUNATCH         49
    #define ENOCSI          50
    #define EL2HLT          51
    #define EBADE           52
    #define EBADR           53
    #define EXFULL          54
    #define ENOANO          55
    #define EBADRQC         56
    #define EBADSLT         57
    #define EDEADLOCK       EDEADLK
    #define EBFONT          59
    #define ENOSTR          60
    #define ENODATA         61
    #define ETIME           62
    #define ENOSR           63
    #define ENONET          64
    #define ENOPKG          65
    #define EREMOTE         66
    #define ENOLINK         67
    #define EADV            68
    #define ESRMNT          69
    #define ECOMM           70
    #define EPROTO          71
    #define EMULTIHOP       72
    #define EDOTDOT         73
    #define EBADMSG         74
    #define EOVERFLOW       75
    #define ENOTUNIQ        76
    #define EBADFD          77
    #define EREMCHG         78
    #define ELIBACC         79
    #define ELIBBAD         80
    #define ELIBSCN         81
    #define ELIBMAX         82
    #define ELIBEXEC        83
    #define EILSEQ          84
    #define ERESTART        85
    #define ESTRPIPE        86
    #define EUSERS          87
    #define ENOTSOCK        88
    #define EDESTADDRREQ    89
    #define EMSGSIZE        90
    #define EPROTOTYPE      91
    #define ENOPROTOOPT     92
    #define EPROTONOSUPPORT 93
    #define ESOCKTNOSUPPORT 94
    #define EOPNOTSUPP      95
    #define ENOTSUP         EOPNOTSUPP
    #define EPFNOSUPPORT    96
    #define EAFNOSUPPORT    97
    #define EADDRINUSE      98
    #define EADDRNOTAVAIL   99
    #define ENETDOWN        100
    #define ENETUNREACH     101
    #define ENETRESET       102
    #define ECONNABORTED    103
    #define ECONNRESET      104
    #define ENOBUFS         105
    #define EISCONN         106
    #define ENOTCONN        107
    #define ESHUTDOWN       108
    #define ETOOMANYREFS    109
    #define ETIMEDOUT       110
    #define ECONNREFUSED    111
    #define EHOSTDOWN       112
    #define EHOSTUNREACH    113
    #define EALREADY        114
    #define EINPROGRESS     115
    #define ESTALE          116
    #define EUCLEAN         117
    #define ENOTNAM         118
    #define ENAVAIL         119
    #define EISNAM          120
    #define EREMOTEIO       121
    #define EDQUOT          122
    #define ENOMEDIUM       123
    #define EMEDIUMTYPE     124
    #define ECANCELED       125
    #define ENOKEY          126
    #define EKEYEXPIRED     127
    #define EKEYREVOKED     128
    #define EKEYREJECTED    129
    #define EOWNERDEAD      130
    #define ENOTRECOVERABLE 131
    #define ERFKILL         132
    #define EHWPOISON       133
    #ifdef __GNUC__
    __attribute__((const))
#endif
    int *__errno_location(void);
    #define errno (*__errno_location())
    #ifdef _GNU_SOURCE
    extern char *program_invocation_short_name, *program_invocation_name;
#endif
#endif
EOF
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_HOST = "aarch64.*-linux"

# optee-spdevkit static library is part of optee-os image. No need to package this library in a staticdev package
INSANE_SKIP:${PN}-dev = "staticdev"
# Build paths are currently embedded
INSANE_SKIP:${PN}-dev += "buildpaths"

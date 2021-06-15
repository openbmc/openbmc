require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash51-001;apply=yes;striplevel=0;name=patch001 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash51-002;apply=yes;striplevel=0;name=patch002 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash51-003;apply=yes;striplevel=0;name=patch003 \
           ${GNU_MIRROR}/bash/bash-${PV}-patches/bash51-004;apply=yes;striplevel=0;name=patch004 \
           file://execute_cmd.patch \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://run-bash-ptests \
           file://fix-run-builtins.patch \
           file://use_aclocal.patch \
           file://makerace.patch \
           "

SRC_URI[tarball.sha256sum] = "cc012bc860406dcf42f64431bcd3d2fa7560c02915a601aba9cd597a39329baa"
SRC_URI[patch001.sha256sum] = "ebb07b3dbadd98598f078125d0ae0d699295978a5cdaef6282fe19adef45b5fa"
SRC_URI[patch002.sha256sum] = "15ea6121a801e48e658ceee712ea9b88d4ded022046a6147550790caf04f5dbe"
SRC_URI[patch003.sha256sum] = "22f2cc262f056b22966281babf4b0a2f84cb7dd2223422e5dcd013c3dcbab6b1"
SRC_URI[patch004.sha256sum] = "9aaeb65664ef0d28c0067e47ba5652b518298b3b92d33327d84b98b28d873c86"

DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

BBCLASSEXTEND = "nativesdk"

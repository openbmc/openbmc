SUMMARY = "mailx is the traditional command-line-mode mail user agent"

DESCRIPTION = "Mailx is derived from Berkeley Mail and is intended provide the \
functionality of the POSIX mailx command with additional support \
for MIME, IMAP, POP3, SMTP, and S/MIME."

HOMEPAGE = "http://heirloom.sourceforge.net/mailx.html"
SECTION = "console/network"
LICENSE = "BSD & MPL-1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4202a0a62910cf94f7af8a3436a2a2dd"

DEPENDS = "openssl"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/h/heirloom-mailx/heirloom-mailx_12.5.orig.tar.gz;name=archive \
           file://0001-Don-t-reuse-weak-symbol-optopt-to-fix-FTBFS-on-mips.patch \
           file://0002-Patched-out-SSL2-support-since-it-is-no-longer-suppo.patch \
           file://0003-Fixed-Lintian-warning-warning-macro-N-not-defined.patch \
           file://0011-outof-Introduce-expandaddr-flag.patch \
           file://0012-unpack-Disable-option-processing-for-email-addresses.patch \
           file://0013-fio.c-Unconditionally-require-wordexp-support.patch \
           file://0014-globname-Invoke-wordexp-with-WRDE_NOCMD.patch \
           file://0015-usr-sbin-sendmail.patch \
           file://explicitly.disable.krb5.support.patch \
          "

SRC_URI[archive.md5sum] = "29a6033ef1412824d02eb9d9213cb1f2"
SRC_URI[archive.sha256sum] = "015ba4209135867f37a0245d22235a392b8bbed956913286b887c2e2a9a421ad"

# for this package we're mostly interested in tracking debian patches,
# and not in the upstream version where all development has effectively stopped
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/h/heirloom-mailx/"
UPSTREAM_CHECK_REGEX = "(?P<pver>((\d+\.*)+)-((\d+\.*)+))\.(diff|debian\.tar)\.(gz|xz)"

S = "${WORKDIR}/heirloom-mailx-12.5"

inherit autotools-brokensep

CFLAGS_append = " -D_BSD_SOURCE -DDEBIAN -I${S}/EXT"

# "STRIP=true" means that 'true' command will be used to 'strip' files which will achieve the effect of not stripping them
# mailx's Makefile doesn't allow a more straightforward way to avoid stripping
EXTRA_OEMAKE = "SENDMAIL=${sbindir}/sendmail IPv6=-DHAVE_IPv6_FUNCS PREFIX=/usr UCBINSTALL=/usr/bin/install STRIP=true"

# The makeconfig can't run parallelly, otherwise the checking results
# might be incorrect and lead to errors:
# fio.c:56:17: fatal error: ssl.h: No such file or directory
# #include <ssl.h>
PARALLEL_MAKE = ""

# Causes gcc to get stuck and eat all available memory in qemuarm builds
# http://errors.yoctoproject.org/Errors/Details/20488/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

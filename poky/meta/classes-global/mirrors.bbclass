#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

MIRRORS += "\
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian/20180310T215105Z/pool \
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20120328T092752Z/debian/pool \
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20110127T084257Z/debian/pool \
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20090802T004153Z/debian/pool \
${DEBIAN_MIRROR}	http://ftp.de.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.au.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.cl.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.hr.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.fi.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.hk.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.hu.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.ie.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.it.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.jp.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.no.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.pl.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.ro.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.si.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.es.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.se.debian.org/debian/pool \
${DEBIAN_MIRROR}	http://ftp.tr.debian.org/debian/pool \
${GNU_MIRROR}	https://mirrors.kernel.org/gnu \
${KERNELORG_MIRROR}	http://www.kernel.org/pub \
${GNUPG_MIRROR}	ftp://ftp.gnupg.org/gcrypt \
${GNUPG_MIRROR}	ftp://ftp.franken.de/pub/crypt/mirror/ftp.gnupg.org/gcrypt \
${GNUPG_MIRROR}	ftp://mirrors.dotsrc.org/gcrypt \
ftp://dante.ctan.org/tex-archive ftp://ftp.fu-berlin.de/tex/CTAN \
ftp://dante.ctan.org/tex-archive http://sunsite.sut.ac.jp/pub/archives/ctan/ \
ftp://dante.ctan.org/tex-archive http://ctan.unsw.edu.au/ \
ftp://ftp.gnutls.org/gcrypt/gnutls ${GNUPG_MIRROR}/gnutls \
http://ftp.info-zip.org/pub/infozip/src/ ftp://sunsite.icm.edu.pl/pub/unix/archiving/info-zip/src/ \
http://www.mirrorservice.org/sites/lsof.itap.purdue.edu/pub/tools/unix/lsof/ http://www.mirrorservice.org/sites/lsof.itap.purdue.edu/pub/tools/unix/lsof/OLD/ \
${APACHE_MIRROR}  http://www.us.apache.org/dist \
${APACHE_MIRROR}  http://archive.apache.org/dist \
http://downloads.sourceforge.net/watchdog/ http://fossies.org/linux/misc/ \
${SAVANNAH_GNU_MIRROR} http://download-mirror.savannah.gnu.org/releases \
${SAVANNAH_NONGNU_MIRROR} http://download-mirror.savannah.nongnu.org/releases \
ftp://sourceware.org/pub http://mirrors.kernel.org/sourceware \
ftp://sourceware.org/pub http://gd.tuwien.ac.at/gnu/sourceware \
ftp://sourceware.org/pub http://ftp.gwdg.de/pub/linux/sources.redhat.com/sourceware \
cvs://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
svn://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
git://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
gitsm://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \
hg://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \
bzr://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
p4://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \
osc://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
https?://.*/.*  http://downloads.yoctoproject.org/mirror/sources/ \
ftp://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \
npm://.*/?.*    http://downloads.yoctoproject.org/mirror/sources/ \
cvs://.*/.*     http://sources.openembedded.org/ \
svn://.*/.*     http://sources.openembedded.org/ \
git://.*/.*     http://sources.openembedded.org/ \
gitsm://.*/.*   http://sources.openembedded.org/ \
hg://.*/.*      http://sources.openembedded.org/ \
bzr://.*/.*     http://sources.openembedded.org/ \
p4://.*/.*      http://sources.openembedded.org/ \
osc://.*/.*     http://sources.openembedded.org/ \
https?://.*/.*  http://sources.openembedded.org/ \
ftp://.*/.*     http://sources.openembedded.org/ \
npm://.*/?.*    http://sources.openembedded.org/ \
${CPAN_MIRROR}  https://cpan.metacpan.org/ \
https?://downloads.yoctoproject.org/releases/uninative/ https://mirrors.kernel.org/yocto/uninative/ \
https?://downloads.yoctoproject.org/mirror/sources/ https://mirrors.kernel.org/yocto-sources/ \
"

# Use MIRRORS to provide git repo fallbacks using the https protocol, for cases
# where git native protocol fetches may fail due to local firewall rules, etc.

MIRRORS += "\
git://salsa.debian.org/.*     git://salsa.debian.org/PATH;protocol=https \
git://git.gnome.org/.*        git://gitlab.gnome.org/GNOME/PATH;protocol=https \
git://git.infradead.org/.*    git://git.infraroot.at/PATH;protocol=https \
git://.*/.*                   git://HOST/PATH;protocol=https \
git://.*/.*                   git://HOST/git/PATH;protocol=https \
"

# Switch llvm, glibc and binutils recipes to use shallow clones as they're large and this
# improves user experience whilst allowing the flexibility of git urls in the recipes
BB_GIT_SHALLOW:pn-binutils = "1"
BB_GIT_SHALLOW:pn-binutils-cross-${TARGET_ARCH} = "1"
BB_GIT_SHALLOW:pn-binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = "1"
BB_GIT_SHALLOW:pn-binutils-testsuite = "1"
BB_GIT_SHALLOW:pn-binutils-crosssdk-${SDK_SYS} = "1"
BB_GIT_SHALLOW:pn-binutils-native = "1"
BB_GIT_SHALLOW:pn-nativesdk-binutils = "1"

BB_GIT_SHALLOW:pn-cross-localedef-native = "1"
BB_GIT_SHALLOW:pn-glibc = "1"
BB_GIT_SHALLOW:pn-glibc-tests = "1"
PREMIRRORS += "git://sourceware.org/git/glibc.git https://downloads.yoctoproject.org/mirror/sources/ \
              git://sourceware.org/git/binutils-gdb.git https://downloads.yoctoproject.org/mirror/sources/"

BB_GIT_SHALLOW:pn-llvm = "1"
BB_GIT_SHALLOW:pn-llvm-native = "1"
BB_GIT_SHALLOW:pn-nativesdk-llvm = "1"

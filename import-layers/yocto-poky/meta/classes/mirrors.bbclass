MIRRORS += "\
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20120328T092752Z/debian/pool \n \
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20110127T084257Z/debian/pool \n \
${DEBIAN_MIRROR}	http://snapshot.debian.org/archive/debian-archive/20090802T004153Z/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.de.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.au.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.cl.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.hr.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.fi.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.hk.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.hu.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.ie.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.it.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.jp.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.no.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.pl.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.ro.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.si.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.es.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.se.debian.org/debian/pool \n \
${DEBIAN_MIRROR}	http://ftp.tr.debian.org/debian/pool \n \
${GNU_MIRROR}	https://mirrors.kernel.org/gnu \n \
${KERNELORG_MIRROR}	http://www.kernel.org/pub \n \
${GNUPG_MIRROR}	ftp://ftp.gnupg.org/gcrypt \n \
${GNUPG_MIRROR}	ftp://ftp.franken.de/pub/crypt/mirror/ftp.gnupg.org/gcrypt \n \
${GNUPG_MIRROR}	ftp://mirrors.dotsrc.org/gcrypt \n \
ftp://dante.ctan.org/tex-archive ftp://ftp.fu-berlin.de/tex/CTAN \n \
ftp://dante.ctan.org/tex-archive http://sunsite.sut.ac.jp/pub/archives/ctan/ \n \
ftp://dante.ctan.org/tex-archive http://ctan.unsw.edu.au/ \n \
ftp://ftp.gnutls.org/gcrypt/gnutls ${GNUPG_MIRROR}/gnutls \n \
http://ftp.info-zip.org/pub/infozip/src/ http://mirror.switch.ch/ftp/mirror/infozip/src/ \n \
http://ftp.info-zip.org/pub/infozip/src/ ftp://sunsite.icm.edu.pl/pub/unix/archiving/info-zip/src/ \n \
http://www.mirrorservice.org/sites/lsof.itap.purdue.edu/pub/tools/unix/lsof/ http://www.mirrorservice.org/sites/lsof.itap.purdue.edu/pub/tools/unix/lsof/OLD/ \n \
${APACHE_MIRROR}  http://www.us.apache.org/dist \n \
${APACHE_MIRROR}  http://archive.apache.org/dist \n \
http://downloads.sourceforge.net/watchdog/ http://fossies.org/linux/misc/ \n \
${SAVANNAH_GNU_MIRROR} http://download-mirror.savannah.gnu.org/releases \n \
${SAVANNAH_NONGNU_MIRROR} http://download-mirror.savannah.nongnu.org/releases \n \
ftp://sourceware.org/pub http://mirrors.kernel.org/sourceware \n \
ftp://sourceware.org/pub http://gd.tuwien.ac.at/gnu/sourceware \n \
ftp://sourceware.org/pub http://ftp.gwdg.de/pub/linux/sources.redhat.com/sourceware \n \
cvs://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
svn://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
git://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
hg://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \n \
bzr://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
p4://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \n \
osc://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
https?$://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \n \
ftp://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \n \
npm://.*/?.*    http://downloads.yoctoproject.org/mirror/sources/ \n \
cvs://.*/.*     http://sources.openembedded.org/ \n \
svn://.*/.*     http://sources.openembedded.org/ \n \
git://.*/.*     http://sources.openembedded.org/ \n \
hg://.*/.*      http://sources.openembedded.org/ \n \
bzr://.*/.*     http://sources.openembedded.org/ \n \
p4://.*/.*      http://sources.openembedded.org/ \n \
osc://.*/.*     http://sources.openembedded.org/ \n \
https?$://.*/.* http://sources.openembedded.org/ \n \
ftp://.*/.*     http://sources.openembedded.org/ \n \
npm://.*/?.*    http://sources.openembedded.org/ \n \
${CPAN_MIRROR}  http://cpan.metacpan.org/ \n \
${CPAN_MIRROR}  http://search.cpan.org/CPAN/ \n \
"

# Use MIRRORS to provide git repo fallbacks using the https protocol, for cases
# where git native protocol fetches may fail due to local firewall rules, etc.

MIRRORS += "\
git://anonscm.debian.org/.*   git://anonscm.debian.org/git/PATH;protocol=https \n \
git://git.gnome.org/.*        git://git.gnome.org/browse/PATH;protocol=https \n \
git://git.savannah.gnu.org/.* git://git.savannah.gnu.org/git/PATH;protocol=https \n \
git://git.yoctoproject.org/.* git://git.yoctoproject.org/git/PATH;protocol=https \n \
git://.*/.*                   git://HOST/PATH;protocol=https \n \
"

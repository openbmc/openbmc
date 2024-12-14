SUMMARY = "GNU Parallel - A shell tool for executing jobs in parallel using one or more computers"
DESCRIPTION = "GNU Parallel is a command-line tool for executing jobs in parallel on one or more computers. \
It allows users to run multiple commands simultaneously, making it easier to process large batches of tasks."

HOMEPAGE = "https://www.gnu.org/software/parallel/"
LICENSE = "CC-BY-SA-4.0 & GFDL-1.3-or-later & GPL-3.0-or-later"

LIC_FILES_CHKSUM = " \
    file://LICENSES/CC-BY-SA-4.0.txt;md5=7130783469368ceb248a4f03e89ea4b8 \
    file://LICENSES/GFDL-1.3-or-later.txt;md5=e0771ae6a62dc8a2e50b1d450fea66b7 \
    file://LICENSES/GPL-3.0-or-later.txt;md5=8da5784ab1c72e63ac74971f88658166 \
"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "1a9e752f42c17ca10bc07d0a63a2ca6edcee532282e55d2b34bd9dd14c978a58"

inherit autotools bash-completion

PACKAGES += "${PN}-zsh-completion"
FILES:${PN}-zsh-completion = "${datadir}/zsh"

RDEPENDS:${PN} = " \
    perl \
    perl-module-file-basename \
    perl-module-file-glob \
    perl-module-file-path \
    perl-module-file-temp \
    perl-module-filehandle \
    perl-module-getopt-long \
    perl-module-io-select \
    perl-module-ipc-open3 \
    perl-module-posix \
    perl-module-symbol \
    perl-module-thread-queue \
    perl-module-threads \
    perl-module-threads-shared \
"

BBCLASSEXTEND = "native"

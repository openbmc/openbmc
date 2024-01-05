SUMMARY = "bash and zsh shell history suggest box - easily view, navigate, search and manage your command history."
HOMEPAGE = "http://dvorka.github.io/hstr/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

DEPENDS = "ncurses readline"

SRC_URI = "https://github.com/dvorka/hstr/releases/download/3.1/hstr-${PV}-tarball.tgz \
           file://0001-configure.ac-Don-t-use-AC_CHECK_FILE.patch \
           file://0001-Use-OE-specific-checks-for-ncurses.patch"

S = "${WORKDIR}/${BPN}"

SRC_URI[sha256sum] = "4dabf61f045f022bac8bc909e5fd96041af6c53df56d97dfa3cfbf49af4453a5"

inherit autotools pkgconfig bash-completion

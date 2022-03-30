SUMMARY = "bash and zsh shell history suggest box - easily view, navigate, search and manage your command history."
HOMEPAGE = "http://dvorka.github.io/hstr/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

DEPENDS = "ncurses readline"

SRC_URI = "https://github.com/dvorka/hstr/releases/download/2.5/hstr-${PV}-tarball.tgz \
           file://0001-configure.ac-Don-t-use-AC_CHECK_FILE.patch \
           file://0001-Use-OE-specific-checks-for-ncurses.patch"

S = "${WORKDIR}/${BPN}"

SRC_URI[sha256sum] = "44bb6d93ef064536218f8ae5464772861bfccfe364a436397d9f770207cd306d"

inherit autotools pkgconfig bash-completion

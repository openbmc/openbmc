SUMMARY = "bash and zsh shell history suggest box - easily view, navigate, search and manage your command history."
HOMEPAGE = "http://dvorka.github.io/hstr/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

DEPENDS = "ncurses readline"

SRC_URI = "https://github.com/dvorka/hstr/releases/download/v3.2/hstr-${PV}-tarball.tgz \
           file://0001-configure.ac-Don-t-use-AC_CHECK_FILE.patch \
           file://0001-Use-OE-specific-checks-for-ncurses.patch"

S = "${UNPACKDIR}/${BPN}"

SRC_URI[sha256sum] = "abf0a8625545b2022d62bf0d1c576e3cc783c4ea7cc2ae2843c518743f77f4c9"

UPSTREAM_CHECK_URI = "https://github.com/dvorka/hstr/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig bash-completion

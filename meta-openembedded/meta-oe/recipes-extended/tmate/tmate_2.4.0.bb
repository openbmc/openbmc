SUMMARY = "Instant terminal sharing"
DESCRIPTION = "Tmate is a fork of tmux. It provides an instant pairing solution."
AUTHOR = "Marius Kriegerowski <marius.kriegerowski@gmail.com>"
HOMEPAGE = "https://tmate.io/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=f7d9aab84ec6567139a4755c48d147fb"

DEPENDS:append = " libevent libssh msgpack-c ncurses"
SRC_URI = "\
    git://github.com/tmate-io/tmate.git;protocol=https;branch=master \
"

SRCREV = "5e00bfa5e137e76c81888727712ced2b3fd99f5b"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

require flite.inc

PR = "r1"

EXTRA_OECONF = "--with-audio=alsa --enable-shared"

SRC_URI = "http://www.speech.cs.cmu.edu/flite/packed/flite-${PV}/flite-${PV}-release.tar.gz \
           file://flite-1.3-alsa_support-1.2.diff \
           file://flite-alsa-1.3-configure-with-audio.patch"

SRC_URI[md5sum] = "ae0aca1cb7b4801f4372f3a75a9e52b5"
SRC_URI[sha256sum] = "922225f7001e57a0fbace8833b0a48790a68f6c7b491f2d47c78ad537ab78a8b"

inherit obmc-phosphor-license

HOMEPAGE = "http://github.com/openbmc/skeleton"

SRCREV ??= "1bbc820f2b595b7a674e52bee1775c877b35b61e"
SRC_URI += "git://github.com/openbmc/skeleton"
S = "${WORKDIR}/git/${SKELETON_DIR}"

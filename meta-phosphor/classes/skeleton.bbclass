inherit obmc-phosphor-license

HOMEPAGE = "http://github.com/openbmc/skeleton"

SRCREV ??= "a194b9d150b9a8d35f757bae356083cbf0d1dfd8"
SRC_URI += "git://github.com/openbmc/skeleton"
S = "${WORKDIR}/git/${SKELETON_DIR}"

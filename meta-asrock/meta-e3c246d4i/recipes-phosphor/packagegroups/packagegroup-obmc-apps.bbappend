# e3c246d4i has a 32MB flash and is space-constrained.
#
# NOTE: ${PN} is packagegroup-obmc-apps in this file, which is what
# makes this RDEPENDS removal apply. It must stay here.

# lrzsz is unnecessary
# rsync use scp instead
RDEPENDS:${PN}-devtools:remove:e3c246d4i = "lrzsz rsync"

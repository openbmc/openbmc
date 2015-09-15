require pango.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

SRC_URI += "file://run-ptest \
            file://multilib-fix-clean.patch \
"

SRC_URI[archive.md5sum] = "217a9a753006275215fa9fa127760ece"
SRC_URI[archive.sha256sum] = "18dbb51b8ae12bae0ab7a958e7cf3317c9acfc8a1e1103ec2f147164a0fc2d07"

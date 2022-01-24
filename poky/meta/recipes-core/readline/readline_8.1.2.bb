require readline.inc

SRC_URI += "file://configure-fix.patch \
           file://norpath.patch \
           "

SRC_URI[archive.sha256sum] = "7589a2381a8419e68654a47623ce7dfcb756815c8fee726b98f90bf668af7bc6"

include udev.inc

PR = "r9"

# module-init-tools from kmod_git will provide libkmod runtime
DEPENDS += "module-init-tools"

SRC_URI[md5sum] = "1b964456177fbf48023dfee7db3a708d"
SRC_URI[sha256sum] = "7857ed19fafd8f3ca8de410194e8c7336e9eb8a20626ea8a4ba6449b017faba4"

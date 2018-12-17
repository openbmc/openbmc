require python-pyflame.inc

# v1.6.6
SRCREV = "8a9d8c2acc3b3bb027475b738134f1e6fff14e6c"
SRC_URI += "file://0001-ptrace-Abstract-over-user_regs_struct-name-which-dif.patch"
SRC_URI += "file://0001-symbol-Account-for-prelinked-shared-objects.patch"

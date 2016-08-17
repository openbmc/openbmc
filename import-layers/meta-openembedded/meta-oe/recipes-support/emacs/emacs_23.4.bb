require emacs.inc

PR = "r4"

PNBLACKLIST[emacs] ?= "qemu: uncaught target signal 11 (Segmentation fault) - core dumped"

SRC_URI = "${GNU_MIRROR}/emacs/emacs-${PV}.tar.gz;name=tarball \
           file://use-qemu.patch \
           file://nostdlib-unwind.patch \
"
SRC_URI[tarball.md5sum] = "34405165fcd978fbc8b304cbd99ccf4f"
SRC_URI[tarball.sha256sum] = "b9a2b8434052771f797d2032772eba862ff9aa143029efc72295170607289c18"

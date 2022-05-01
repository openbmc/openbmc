SUMMARY = "nbdkit is a toolkit for creating NBD servers."
DESCRIPTION = "NBD — Network Block Device — is a protocol \
for accessing Block Devices (hard disks and disk-like things) \
over a Network. \
\
nbdkit is a toolkit for creating NBD servers."

HOMEPAGE = "https://github.com/libguestfs/nbdkit"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f9dcc2d8acdde215fa4bd6ac12bb14f0"

SRC_URI = "git://github.com/libguestfs/nbdkit.git;protocol=https;branch=master \
"

SRCREV = "b59380e061fdf0f114c13c226ea2a508f2c067d0"

S = "${WORKDIR}/git"

DEPENDS = "curl xz e2fsprogs zlib"

# autotools-brokensep is needed as nbdkit does not support build in external directory
inherit pkgconfig python3native perlnative bash-completion autotools-brokensep

# Those are required to build standalone
EXTRA_OECONF = " --without-libvirt --without-libguestfs --disable-perl"

# Disable some extended support (not desired for small embedded systems)
#EXTRA_OECONF += " --disable-python"
#EXTRA_OECONF += " --disable-ocaml"
#EXTRA_OECONF += " --disable-rust"
#EXTRA_OECONF += " --disable-ruby"
#EXTRA_OECONF += " --disable-tcl"
#EXTRA_OECONF += " --disable-lua"
#EXTRA_OECONF += " --disable-vddk"

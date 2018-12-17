DESCRIPTION = "libblockdev is a C library supporting GObject introspection for manipulation of \
block devices. It has a plugin-based architecture where each technology (like \
LVM, Btrfs, MD RAID, Swap,...) is implemented in a separate plugin, possibly \
with multiple implementations (e.g. using LVM CLI or the new LVM DBus API)."
HOMEPAGE = "http://rhinstaller.github.io/libblockdev/"
LICENSE = "LGPLv2+"
SECTION = "devel/lib"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c07cb499d259452f324bb90c3067d85c"

inherit autotools python3native gobject-introspection

SRCREV = "0debeb45562ac3d8f6f43f6f942b238abab55be9"
SRC_URI = " \
    git://github.com/rhinstaller/libblockdev;branch=master \
"

S = "${WORKDIR}/git"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"

PACKAGECONFIG ??= "python3 lvm dm kmod parted fs escrow btrfs crypto mdraid kbd mpath"
PACKAGECONFIG[python3] = "--with-python3, --without-python3,,python3"
PACKAGECONFIG[python2] = "--with-python2, --without-python2,,python2"
PACKAGECONFIG[lvm] = "--with-lvm, --without-lvm, multipath-tools, lvm2"
PACKAGECONFIG[lvm-dbus] = "--with-lvm_dbus, --without-lvm_dbus, multipath-tools, lvm2"
PACKAGECONFIG[dm] = "--with-dm, --without-dm, multipath-tools, lvm2"
PACKAGECONFIG[dmraid] = "--with-dmraid, --without-dmraid"
PACKAGECONFIG[kmod] = "--with-kbd, --without-kbd, kmod"
PACKAGECONFIG[parted] = "--with-part, --without-part, parted"
PACKAGECONFIG[fs] = "--with-fs, --without-fs, util-linux"
PACKAGECONFIG[doc] = "--with-gtk-doc, --without-gtk-doc, gtk-doc-native"
PACKAGECONFIG[nvdimm] = "--with-nvdimm, --without-nvdimm"
PACKAGECONFIG[vdo] = "--with-vdo, --without-vdo"
PACKAGECONFIG[escrow] = "--with-escrow, --without-escrow, nss volume-key"
PACKAGECONFIG[btrfs] = "--with-btrfs,--without-btrfs,libbytesize btrfs-tools"
PACKAGECONFIG[crypto] = "--with-crypto,--without-crypto,cryptsetup nss volume-key"
PACKAGECONFIG[mdraid] = "--with-mdraid,--without-mdraid,libbytesize"
PACKAGECONFIG[kbd] = "--with-kbd,--without-kbd,libbytesize"
PACKAGECONFIG[mpath] = "--with-mpath,--without-mpath, multipath-tools, lvm2"

export GIR_EXTRA_LIBS_PATH="${B}/src/utils/.libs"


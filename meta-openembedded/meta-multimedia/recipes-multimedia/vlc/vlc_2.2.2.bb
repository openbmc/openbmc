require ${BPN}.inc

# work around build failure
EXTRA_OECONF += " --enable-libxml2=no"

SRC_URI += "file://0001-enable-subdir-objects.patch \
            file://0002-glibc-does-not-provide-strlcpy.patch \
            file://0003-use-am-path-libgcrypt.patch \
            file://0004-modules-gui-qt4-out-of-tree-build.patch \
            file://0005-libpostproc-header-check.patch \
            file://0006-make-opencv-configurable.patch \
            file://0007-use-vorbisidec.patch \
            file://0008-fix-luaL-checkint.patch \
            file://0009-Avcodec-swscale-use-AV_PIX_FMT-consistently.patch \
            file://0010-SWSCALE-fix-compilation-with-4.x.patch \
"

SRC_URI[md5sum] = "f98d60f0f59ef72b6e3407f2ff09bda6"
SRC_URI[sha256sum] = "9ad23128be16f9b40ed772961272cb0748ed8e4aa1bc79c129e589feebea5fb5"

DESCRIPTION = "A tool for transfer files to/from any OBEX enabled device"
LICENSE = "GPLv2 & PD & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LGPL-2.1.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://GPL-2.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://License.txt;md5=fcbddc3c1debed80dd80da2d3e5f0dc1 \
                   "

DEPENDS += "openobex obexftp-native"
SRC_URI = "http://downloads.sourceforge.net/openobex/${BP}-Source.tar.gz \
           file://Remove_some_printf_in_obexftpd.patch \
           file://0001-apps-CMakeLists.txt-Explicitly-link-libbfb-and-libmu.patch \
           file://make_fuse_swig_optional.patch \
"
SRC_URI[md5sum] = "157a9d1b2ed220203f7084db906de73c"
SRC_URI[sha256sum] = "d40fb48e0a0eea997b3e582774b29f793919a625d54b87182e31a3f3d1c989a3"

inherit cmake pkgconfig

PACKAGECONFIG ?= ""
# fuse support will need meta-filesystems layer
PACKAGECONFIG[fuse] = "-DENABLE_FUSE=ON,-DENABLE_FUSE=OFF,fuse"
PACKAGECONFIG[swig] = "-DENABLE_SWIG=ON,-DENABLE_SWIG=OFF,swig"

DEPENDS_remove_class-native = "fuse-native"

S = "${WORKDIR}/${BP}-Source"

EXTRA_OECMAKE += "-DCMAKE_SKIP_RPATH=ON \
                  -DENABLE_PERL=OFF -DENABLE_PYTHON=OFF \
                  -DENABLE_RUBY=OFF -DENABLE_TCL=OFF \
"

do_compile_class-native () {
    oe_runmake crctable
}

do_install_class-native () {
    install -D -m 0755 ${B}/bfb/crctable ${D}${bindir}/crctable
}

BBCLASSEXTEND = "native"

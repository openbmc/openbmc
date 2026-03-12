SUMMARY = "Management suite for extremely large and complex data collections"
DESCRIPTION = "Unique technology suite that makes possible the management of \
extremely large and complex data collections"
HOMEPAGE = "https://www.hdfgroup.org/"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71a191398102f76050a4e56e78cb4891"

inherit cmake pkgconfig siteinfo qemu multilib_header multilib_script

DEPENDS += "qemu-native zlib"

# set downloadfilename to workaround mirroring test issues. When hdf5 is
# upgraded beyond 2.0.0 this can likely be removed
SRC_URI = "https://support.hdfgroup.org/releases/hdf5/v2_0/v2_0_0/downloads/${BPN}-${PV}.tar.gz;downloadfilename=${BPN}-${PV}-1.tar.gz \
           file://0002-Remove-suffix-shared-from-shared-library-name.patch \
           file://0001-cmake-remove-build-flags.patch \
           "
SRC_URI[sha256sum] = "f4c2edc5668fb846627182708dbe1e16c60c467e63177a75b0b9f12c19d7efed"

FILES:${PN} += "${libdir}/libhdf5.settings ${datadir}/*"

EXTRA_OECMAKE = " \
    -DHDF5_INSTALL_CMAKE_DIR=${libdir}/cmake \
    -DCMAKE_INSTALL_PREFIX='${prefix}' \
    -DHDF5_INSTALL_LIB_DIR='${baselib}' \
"
EXTRA_OECMAKE:prepend:class-target = "-DCMAKE_CROSSCOMPILING_EMULATOR=${WORKDIR}/qemuwrapper "

gen_emu() {
        # Write out a qemu wrapper that will be used by cmake
        # so that it can run target helper binaries through that.
        qemu_binary="${@qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'), [d.expand('${STAGING_DIR_HOST}${libdir}'),d.expand('${STAGING_DIR_HOST}${base_libdir}')])}"
        cat > ${WORKDIR}/qemuwrapper << EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
        chmod +x ${WORKDIR}/qemuwrapper
}
gen_emu:class-native = ""

do_unpack[postfuncs] += "gen_emu"

do_configure:append() {
    sed -i -e 's|${WORKDIR}||g' ${B}/src/libhdf5.settings
    sed -i -e 's|${WORKDIR}||g' ${B}/src/H5build_settings.c
}

do_install:append() {
    # Used for generating config files on target
    oe_multilib_header H5pubconf.h
    sed -i -e 's|${RECIPE_SYSROOT_NATIVE}||g' ${D}${bindir}/h5cc
}

MULTILIB_SCRIPTS += "${PN}:${bindir}/h5cc"

BBCLASSEXTEND = "native"

# h5fuse.sh script needs bash
RDEPENDS:${PN} += "bash"

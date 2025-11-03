SUMMARY = "Management suite for extremely large and complex data collections"
DESCRIPTION = "Unique technology suite that makes possible the management of \
extremely large and complex data collections"
HOMEPAGE = "https://www.hdfgroup.org/"
SECTION = "libs"

LICENSE = "HDF5"
LIC_FILES_CHKSUM = "file://COPYING;md5=adebb1ecf1b3b80c13359e18ef67301e"

inherit cmake siteinfo qemu multilib_header multilib_script

DEPENDS += "qemu-native zlib"

SRC_URI = " \
    https://support.hdfgroup.org/ftp/HDF5/releases/hdf5-1.14/hdf5-1.14.4/src/${BPN}-${PV}.tar.gz \
    file://0002-Remove-suffix-shared-from-shared-library-name.patch \
    file://0001-cmake-remove-build-flags.patch \
    file://CVE-2025-2913.patch \
    file://CVE-2025-2914.patch \
    file://CVE-2025-2915.patch \
    file://CVE-2025-2923-CVE-2025-6816-CVE-2025-6856.patch \
    file://CVE-2025-2924.patch \
    file://CVE-2025-2925.patch \
    file://CVE-2025-6269-CVE-2025-6270-CVE-2025-6516_01.patch \
    file://CVE-2025-6269-CVE-2025-6270-CVE-2025-6516_02.patch \
"
SRC_URI[sha256sum] = "019ac451d9e1cf89c0482ba2a06f07a46166caf23f60fea5ef3c37724a318e03"

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

do_unpack[postfuncs] += "gen_emu"

MULTILIB_SCRIPTS += "${PN}:${bindir}/h5cc \
                     ${PN}:${bindir}/h5hlcc \
"

do_configure:append() {
    sed -i -e 's|${WORKDIR}||g' ${B}/src/libhdf5.settings
    sed -i -e 's|${WORKDIR}||g' ${B}/src/H5build_settings.c
}

do_install:append() {
    # Used for generating config files on target
    oe_multilib_header H5pubconf.h
    # remove the buildpath
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/pkgconfig/hdf5.pc
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/cmake/hdf5-targets.cmake
    sed -i -e 's|${RECIPE_SYSROOT_NATIVE}||g' ${D}${bindir}/h5hlcc
    sed -i -e 's|${RECIPE_SYSROOT_NATIVE}||g' ${D}${bindir}/h5cc
}

BBCLASSEXTEND = "native"

# h5fuse.sh script needs bash
RDEPENDS:${PN} += "bash"

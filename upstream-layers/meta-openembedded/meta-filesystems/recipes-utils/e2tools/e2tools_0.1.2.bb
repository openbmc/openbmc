SUMMARY = "Set of GPL'ed utilities to ext2/ext3 filesystem."
DESCRIPTION = "e2tools is a simple set of GPL'ed utilities to read, write, \
and manipulate files in an ext2/ext3 filesystem. These utilities access a \
filesystem directly using the ext2fs library. Can also be used on a Linux \
machine to read/write to disk images or floppies without having to mount \
them or have root access."
HOMEPAGE = "https://github.com/e2tools/e2tools"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "coreutils e2fsprogs"

SRC_URI = " \
           git://github.com/e2tools/e2tools;protocol=https;branch=master \
           file://run-ptest \
"

SRCREV = "46da6c398e70635a296245851fcedbc56c35e824"


inherit autotools pkgconfig ptest

# Otherwise these tools will be detected from build host and
# assumptions will go wrong, Fun of cross compiling
EXTRA_OECONF += "\
                ac_cv_path_MKE2FS=${base_sbindir}/mke2fs \
                ac_cv_path_CHMOD=${base_bindir}/chmod \
                ac_cv_path_DD=${base_bindir}/dd \
                ac_cv_path_GREP=${base_bindir}/grep \
                "

do_install_ptest() {
    rm -rf "${D}${PTEST_PATH}/*"
    cp -r ../build "${D}${PTEST_PATH}"
    cp -r "${S}/build-aux" "${D}${PTEST_PATH}/build"
    cp -r "${UNPACKDIR}" "${D}${PTEST_PATH}/"
    rm -rf ${D}${PTEST_PATH}/build/config.log \
        ${D}${PTEST_PATH}/build/autom4te.cache \
        ${D}${PTEST_PATH}/*/*/.git ${D}${PTEST_PATH}/*/*/.github \
        ${D}${PTEST_PATH}/*/*/autom4te.cache

    # config.status contains WORKDIR paths in strings literal split across
    # multiple lines. ie
    # S["foo"]="/path/to/work"\
    # "dir/"
    # These line splits prevent the following sed from working properly.
    # Make a first pass on the file to undo any line split
    sed '
        :a;N;$!ba;  # Read the whole file into the pattern buffer
        s/"\\\n"//g # Delete "\<newline>" ie literal string line break
        ' \
        -i ${D}${PTEST_PATH}/build/config.status

    # Now remove/replace the non-reproducible paths
    sed -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*--sysroot=[^ "]*@@g' \
        -e 's@[^ ]*--with-libtool-sysroot=[^ "]*@@g' \
        -e 's@[^ ]*--with-install-prefix=[^ "]*@@g' \
        -e '/EXT2FS_CFLAGS/d' \
        -e '/LDFLAGS/d' \
        -e '/PKG_CONFIG_PATH/d' \
        -e '/PKG_CONFIG_LIBDIR/d' \
        -e 's@${S}@${PTEST_PATH}@g' \
        -e 's@${B}@${PTEST_PATH}/build@g' \
        -e 's@${HOSTTOOLS_DIR}@@g' \
        -e 's@${RECIPE_SYSROOT_NATIVE}@@g' \
        -e 's@${RECIPE_SYSROOT}@@g' \
        -i ${D}${PTEST_PATH}/build/config.status
}

RDEPENDS:${PN}-ptest += "bash coreutils e2fsprogs e2tools gawk make perl"

BBCLASSEXTEND = "native"

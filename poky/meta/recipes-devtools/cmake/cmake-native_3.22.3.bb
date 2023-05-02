require cmake.inc
inherit native

DEPENDS += "bzip2-replacement-native xz-native zlib-native curl-native ncurses-native zstd-native"

SRC_URI += "file://OEToolchainConfig.cmake \
            file://environment.d-cmake.sh \
            file://0001-CMakeDetermineSystem-use-oe-environment-vars-to-load.patch \
            file://0005-Disable-use-of-ext2fs-ext2_fs.h-by-cmake-s-internal-.patch \
            "

LICENSE:append = " & BSD-1-Clause & MIT & BSD-2-Clause"
LIC_FILES_CHKSUM:append = " \
    file://Utilities/cmjsoncpp/LICENSE;md5=fa2a23dd1dc6c139f35105379d76df2b \
    file://Utilities/cmlibarchive/COPYING;md5=d499814247adaee08d88080841cb5665 \
    file://Utilities/cmexpat/COPYING;md5=9e2ce3b3c4c0f2670883a23bbd7c37a9 \
    file://Utilities/cmlibrhash/COPYING;md5=a8c2a557a5c53b1c12cddbee98c099af \
    file://Utilities/cmlibuv/LICENSE;md5=a68902a430e32200263d182d44924d47 \
"

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

CMAKE_EXTRACONF = "\
    -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR_NATIVE} \
    -DBUILD_CursesDialog=1 \
    -DCMAKE_USE_SYSTEM_LIBRARIES=1 \
    -DCMAKE_USE_SYSTEM_LIBRARY_JSONCPP=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBARCHIVE=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBUV=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_LIBRHASH=0 \
    -DCMAKE_USE_SYSTEM_LIBRARY_EXPAT=0 \
    -DENABLE_ACL=0 -DHAVE_ACL_LIBACL_H=0 \
    -DHAVE_SYS_ACL_H=0 \
    -DCURL_LIBRARIES=-lcurl \
"

do_configure () {
	${S}/configure --verbose --prefix=${prefix} \
		${@oe.utils.parallel_make_argument(d, '--parallel=%d')} \
		${@bb.utils.contains('CCACHE', 'ccache ', '--enable-ccache', '', d)} \
		-- ${CMAKE_EXTRACONF}
}

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install

	# The following codes are here because eSDK needs to provide compatibilty
	# for SDK. That is, eSDK could also be used like traditional SDK.
	mkdir -p ${D}${datadir}/cmake
	install -m 644 ${WORKDIR}/OEToolchainConfig.cmake ${D}${datadir}/cmake/
	mkdir -p ${D}${base_prefix}/environment-setup.d
	install -m 644 ${WORKDIR}/environment.d-cmake.sh ${D}${base_prefix}/environment-setup.d/cmake.sh

	# Help docs create tons of files in the native sysroot and aren't needed there
	rm -rf ${D}${datadir}/cmake-*/Help
}

do_compile[progress] = "percent"

SYSROOT_DIRS_NATIVE += "${datadir}/cmake ${base_prefix}/environment-setup.d"
